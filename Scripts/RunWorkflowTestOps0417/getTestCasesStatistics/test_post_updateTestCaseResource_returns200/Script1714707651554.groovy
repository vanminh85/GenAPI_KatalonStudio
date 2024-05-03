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
def projectData = new JsonSlurper().parseText(projectResponse.getResponseText())

// Step 3
def testProjectPayload = '{"name": "Test Test Project", "projectId": ' + projectData["id"] + ', "batch": "batch", "folderPath": "folderPath", "fileName": "fileName", "uploadedPath": "uploadedPath"}'
def testProjectRequest = new RequestObject()
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/upload-script-repo")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)
def testProjectData = new JsonSlurper().parseText(testProjectResponse.getResponseText())

// Step 4
def testCasePayload = '{"name": "Test Case", "path": "testPath", "previousStatus": "PASSED", "alias": "Test Alias", "testModuleId": 123, "webUrl": "http://test-url.com", "description": "Test Description", "project": ' + JsonOutput.toJson(projectData) + ', "testProject": ' + JsonOutput.toJson(testProjectData) + ', "type": "TEST_CASE", "testType": "G4_TEST_CASE", "urlId": "testId"}'
def testCaseRequest = new RequestObject()
testCaseRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testCasePayload)))
testCaseRequest.setRestUrl("https://testops.katalon.io/api/v1/test-cases/update")
testCaseRequest.setRestRequestMethod("POST")
addAuthHeader(testCaseRequest)
addContentTypeHeader(testCaseRequest)
def testCaseResponse = WSBuiltInKeywords.sendRequest(testCaseRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testCaseResponse, 200)
def testCaseId = new JsonSlurper().parseText(testCaseResponse.getResponseText())["id"]

// Step 5
def updatedTestCasePayload = '{"id": ' + testCaseId + ', "name": "Updated Test Case", "path": "updatedTestPath", "previousStatus": "FAILED", "alias": "Updated Alias", "testModuleId": 456, "webUrl": "http://updated-url.com", "description": "Updated Description", "project": ' + JsonOutput.toJson(projectData) + ', "testProject": ' + JsonOutput.toJson(testProjectData) + ', "type": "SCENARIO", "testType": "MANUAL_TEST_CASE", "urlId": "updatedTestId"}'
def updatedTestCaseRequest = new RequestObject()
updatedTestCaseRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedTestCasePayload)))
updatedTestCaseRequest.setRestUrl("https://testops.katalon.io/api/v1/test-cases/update")
updatedTestCaseRequest.setRestRequestMethod("POST")
addAuthHeader(updatedTestCaseRequest)
addContentTypeHeader(updatedTestCaseRequest)
def updatedTestCaseResponse = WSBuiltInKeywords.sendRequest(updatedTestCaseRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedTestCaseResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

