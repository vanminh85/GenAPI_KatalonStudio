import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
    def authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
    if (authToken) {
        def auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
        request.getHttpHeaderProperties().add(auth_header)
    }
}

def addContentTypeHeader(request) {
    def content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
    request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new Organization
def orgRequest = new RequestObject()
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    name: "Organization__unique__",
    role: "OWNER",
    domain: "example.com"
])))
orgRequest.setBodyContent(orgPayload)
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

// Step 2: Create a new Team
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    name: "Team__unique__",
    role: "OWNER",
    organizationId: orgResponse.getResponseText("id"))
]))
teamRequest.setBodyContent(teamPayload)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 3: Create a new GitRepository
def gitRepoRequest = new RequestObject()
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")
addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)
def gitRepoPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    testProjectId: 1,
    name: "GitRepo__unique__",
    repository: "https://github.com/example/repo",
    branch: "main"
])))
gitRepoRequest.setBodyContent(gitRepoPayload)
def gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
WSBuiltInKeywords.verifyResponseStatusCode(gitRepoResponse, 200)

// Step 4: Create a new TestProject
def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/1")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    name: "TestProject__unique__",
    description: "Test project description",
    defaultTestProject: true,
    uploadFileId: 1,
    projectId: 1,
    teamId: teamResponse.getResponseText("id")),
    type: "GIT",
    gitRepository: [
        id: gitRepoResponse.getResponseText("id"),
        testProjectId: 1,
        name: "GitRepo__unique__",
        repository: "https://github.com/example/repo",
        branch: "main"
    ]
]))
testProjectRequest.setBodyContent(testProjectPayload)
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

// Step 5: Delete the TestProject
def deleteRequest = new RequestObject()
deleteRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/1")
deleteRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteRequest)
def deleteResponse = WSBuiltInKeywords.sendRequest(deleteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
