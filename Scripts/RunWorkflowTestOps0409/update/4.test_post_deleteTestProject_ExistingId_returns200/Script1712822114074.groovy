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

// Step 1: Create a new Team
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = JsonOutput.toJson([
    "name": "TeamName__unique__",
    "role": "OWNER",
    "organizationId": 1
])
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2: Extract the teamId from the response

// Step 3: Create a new Git Repository
def gitRepoRequest = new RequestObject()
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")
addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)
def gitRepoPayload = JsonOutput.toJson([
    "testProjectId": 1,
    "name": "GitRepoName__unique__",
    "repository": "https://github.com/repo",
    "branch": "main"
])
gitRepoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(gitRepoPayload)))
def gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
def gitRepoId = new JsonSlurper().parseText(gitRepoResponse.getResponseText())["id"]

// Step 4: Extract the gitRepository id from the response

// Step 5: Create a new Test Project
def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/1")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = JsonOutput.toJson([
    "name": "TestProjectName__unique__",
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
def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())["id"]

// Step 6: Extract the testProjectId from the response

// Step 7: Delete the Test Project
def deleteRequest = new RequestObject()
deleteRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${testProjectId}")
deleteRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteRequest)
def deleteResponse = WSBuiltInKeywords.sendRequest(deleteRequest)

// Step 8: Verify that the response status code is 200 OK
def statusCode = deleteResponse.getStatusCode()
if (statusCode == 200) {
    println("Test case passed: Response status code is 200 OK")
} else {
    println("Test case failed: Response status code is not 200 OK")
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
