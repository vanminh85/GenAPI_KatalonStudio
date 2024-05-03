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

// Step 1
def teamPayload = '{"name": "Test Team", "role": "OWNER"}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2
def projectPayload = '{"name": "Test Project", "teamId": ' + teamId + '}'
def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

// Step 3
def testProjectPayload = '{"name": "Test Test Project", "projectId": ' + projectId + ', "batch": "batch", "folderPath": "folderPath", "fileName": "fileName", "uploadedPath": "uploadedPath"}'
def testProjectRequest = new RequestObject()
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/upload-script-repo")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)
def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())["id"]

// Step 4
def updatedTestProjectPayload = '{"id": ' + testProjectId + ', "name": "Updated Test Project", "description": "Updated Description", "defaultTestProject": true, "uploadFileId": 123, "projectId": ' + projectId + ', "teamId": ' + teamId + ', "createdAt": "2022-01-01T00:00:00Z", "type": "KS", "dirty": false, "autonomous": true}'
def updatedTestProjectRequest = new RequestObject()
updatedTestProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedTestProjectPayload)))
updatedTestProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/upload-script-repo")
updatedTestProjectRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedTestProjectRequest)
addContentTypeHeader(updatedTestProjectRequest)
def updatedTestProjectResponse = WSBuiltInKeywords.sendRequest(updatedTestProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedTestProjectResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}


