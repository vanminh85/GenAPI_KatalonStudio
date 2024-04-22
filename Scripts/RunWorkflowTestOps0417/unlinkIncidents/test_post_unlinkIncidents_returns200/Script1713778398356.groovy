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

// Step 1: Create a new IncidentResource
def incidentPayload = '{"name": "Test Incident__unique__", "description": "Test Description", "projectId": 1, "teamId": 1, "urlIds": ["url1", "url2"], "executionTestResultIds": [1, 2], "order": 1, "createdAt": "2022-01-01T00:00:00Z", "updatedAt": "2022-01-01T00:00:00Z"}'
def incidentRequest = new RequestObject()
incidentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(incidentPayload)))
incidentRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/incidents")
incidentRequest.setRestRequestMethod("POST")
addAuthHeader(incidentRequest)
addContentTypeHeader(incidentRequest)
def incidentResponse = WSBuiltInKeywords.sendRequest(incidentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(incidentResponse, 200)

// Step 2: Create a new ExecutionTestResultResource
def executionTestResultPayload = '{"incidentId": 1, "projectId": 1, "incidentOrder": 1, "executionTestResultId": 1}'
def executionTestResultRequest = new RequestObject()
executionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionTestResultPayload)))
executionTestResultRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-results/1/incidents")
executionTestResultRequest.setRestRequestMethod("POST")
addAuthHeader(executionTestResultRequest)
addContentTypeHeader(executionTestResultRequest)
def executionTestResultResponse = WSBuiltInKeywords.sendRequest(executionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionTestResultResponse, 200)

// Step 3: Delete the created binding
def deletePayload = '{"id": "1__unique__", "incidentId": 1, "projectId": 1, "incidentOrder": 1, "executionTestResultId": 1}'
def deleteRequest = new RequestObject()
deleteRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(deletePayload)))
deleteRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-results/1/incidents")
deleteRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteRequest)
addContentTypeHeader(deleteRequest)
def deleteResponse = WSBuiltInKeywords.sendRequest(deleteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteResponse, 200)

// Step 4: Verify the response status code is 200
if (deleteResponse.getStatusCode() == 200) {
	println("Test passed")
} else {
	println("Test failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

