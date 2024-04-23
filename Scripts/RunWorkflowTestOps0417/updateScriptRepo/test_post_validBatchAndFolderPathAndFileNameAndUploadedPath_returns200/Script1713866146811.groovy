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
def teamPayload = '{"name": "TeamName__unique__", "role": "OWNER"}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2: Create a new Project
def projectPayload = '{"name": "ProjectName__unique__", "teamId": ' + teamId + '}'
def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

// Step 3: Create a new Test Project
def testProjectPayload = '{"name": "TestProjectName__unique__", "projectId": ' + projectId + ', "teamId": ' + teamId + '}'
def testProjectRequest = new RequestObject()
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
testProjectRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())["id"]

// Step 4: Update script repository for the Test Project
def updateScriptPayload = '{"batch": "BatchValue__unique__", "folderPath": "FolderPathValue__unique__", "fileName": "FileNameValue__unique__", "uploadedPath": "UploadedPathValue__unique__"}'
def updateScriptRequest = new RequestObject()
updateScriptRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateScriptPayload)))
updateScriptRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/${testProjectId}/update-script-repo")
updateScriptRequest.setRestRequestMethod("POST")
addAuthHeader(updateScriptRequest)
addContentTypeHeader(updateScriptRequest)
def updateScriptResponse = WSBuiltInKeywords.sendRequest(updateScriptRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateScriptResponse, 200)

// Step 5: Verify the response status code is 200
assert updateScriptResponse.getStatusCode() == 200

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

