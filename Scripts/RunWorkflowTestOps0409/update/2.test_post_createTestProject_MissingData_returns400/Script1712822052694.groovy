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

def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    name: "TeamName__unique__",
    role: "OWNER",
    organizationId: 1
])))
teamRequest.setBodyContent(teamPayload)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

def gitRepoRequest = new RequestObject()
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")
addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)
def gitRepoPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    testProjectId: 1,
    name: "GitRepoName__unique__",
    repository: "https://github.com/repo",
    branch: "main"
])))
gitRepoRequest.setBodyContent(gitRepoPayload)
def gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
def gitRepoId = new JsonSlurper().parseText(gitRepoResponse.getResponseText())["id"]

def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${teamId}")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    name: "TestProjectName__unique__",
    description: "TestProjectDescription__unique__",
    defaultTestProject: true,
    uploadFileId: 1,
    projectId: 1,
    teamId: teamId,
    createdAt: "2022-01-01T00:00:00Z",
    latestJob: ["id": 1],
    uploadFileName: "test_file",
    type: "KS",
    gitRepository: ["id": gitRepoId],
    testSuiteCollections: [["id": 1]],
    dirty: false,
    autonomous: true
])))
testProjectRequest.setBodyContent(testProjectPayload)
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
def statusCode = testProjectResponse.getStatusCode()

if (statusCode == 400) {
    println("Test passed: Response status code is 400 Bad Request")
} else {
    println("Test failed: Response status code is not 400 Bad Request")
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
