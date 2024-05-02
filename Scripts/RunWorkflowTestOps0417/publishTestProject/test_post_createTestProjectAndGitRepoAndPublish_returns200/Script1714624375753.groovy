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
	"name": "Team1__unique__",
	"role": "OWNER",
	"users": [],
	"organization": [:],
	"organizationId": 1
])
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2: Create a new Project under the Team
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = JsonOutput.toJson([
	"name": "Project1__unique__",
	"teamId": teamId,
	"timezone": "UTC",
	"status": "ACTIVE",
	"canAutoIntegrate": true,
	"sampleProject": false
])
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

// Step 3: Create a new Test Project under the Project
def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = JsonOutput.toJson([
	"name": "TestProject1__unique__",
	"type": "GIT",
	"projectId": projectId
])
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())["id"]

// Step 4: Create a new Git Repository for the Test Project
def gitRepoRequest = new RequestObject()
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")
addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)
def gitRepoPayload = JsonOutput.toJson([
	"testProjectId": testProjectId,
	"name": "GitRepo1__unique__",
	"repository": "https://github.com/example/repo",
	"branch": "main",
	"username": "username",
	"password": "password",
	"accessKeyId": "access_key",
	"secretAccessKey": "secret_key",
	"projectId": projectId,
	"teamId": teamId,
	"vcsType": "GITHUB",
	"shouldMergeTestResultsForNewScriptRepo": false
])
gitRepoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(gitRepoPayload)))
def gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
WSBuiltInKeywords.verifyResponseStatusCode(gitRepoResponse, 200)

// Step 5: Publish the Test Project associated with the Git Repository
def publishRequest = new RequestObject()
publishRequest.setRestUrl("https://testops.katalon.io/api/v1/test-management/test-projects/${testProjectId}/publish")
publishRequest.setRestRequestMethod("POST")
addAuthHeader(publishRequest)
addContentTypeHeader(publishRequest)
def publishPayload = JsonOutput.toJson([
	"message": "Publishing test project",
	"testCases": [],
	"testSuites": []
])
publishRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(publishPayload)))
def publishResponse = WSBuiltInKeywords.sendRequest(publishRequest)
WSBuiltInKeywords.verifyResponseStatusCode(publishResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

