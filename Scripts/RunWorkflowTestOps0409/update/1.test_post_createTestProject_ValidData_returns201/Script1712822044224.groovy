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
def teamPayload = JsonOutput.toJson([
    "name": "Team__unique__",
    "role": "OWNER",
    "organizationId": 1
])
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

def gitRepoRequest = new RequestObject()
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")
addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)
def gitRepoPayload = JsonOutput.toJson([
    "testProjectId": 1,
    "name": "GitRepo__unique__",
    "repository": "https://github.com/example",
    "branch": "main"
])
gitRepoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(gitRepoPayload)))
def gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
def gitRepoId = new JsonSlurper().parseText(gitRepoResponse.getResponseText())["id"]

def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${teamId}")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = JsonOutput.toJson([
    "name": "TestProject__unique__",
    "description": "Test project description",
    "defaultTestProject": true,
    "uploadFileId": 1,
    "projectId": 1,
    "teamId": teamId,
    "type": "GIT",
    "gitRepository": [
        "id": gitRepoId
    ]
])
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
def status = testProjectResponse.getStatusCode()

WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 201)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
