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
def teamPayload = '{"name": "Team__unique__", "role": "OWNER", "users": [], "organizationId": 1}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2: Create a new Project
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"name": "Project__unique__", "teamId": ' + teamId + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

// Step 3: Create a new Test Project with Git Repository
def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample-git-test-project")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = '{"name": "TestProject__unique__", "description": "Test project with Git repository", "defaultTestProject": true, "uploadFileId": 1, "projectId": ' + projectId + ', "teamId": ' + teamId + ', "type": "GIT", "gitRepository": {"url": "https://github.com/example.git", "branch": "main"}}'
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())["id"]

// Step 4: Update the Git Repository of the Test Project
def updateRequest = new RequestObject()
updateRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + testProjectId + "/update-script-repo")
updateRequest.setRestRequestMethod("POST")
addAuthHeader(updateRequest)
addContentTypeHeader(updateRequest)
def updatePayload = '{"batch": "update", "folderPath": "scripts", "fileName": "test_script.py", "uploadedPath": "/path/to/uploaded/script"}'
updateRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatePayload)))
def updateResponse = WSBuiltInKeywords.sendRequest(updateRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

