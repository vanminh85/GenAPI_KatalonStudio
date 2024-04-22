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
def teamPayload = '{"id": 1, "name": "Team__unique__", "role": "OWNER", "organizationId": 1}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 2: Create a new Project
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"id": 1, "name": "Project__unique__", "teamId": 1, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": true}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 3: Create a new ExecutionResource
def executionRequest = new RequestObject()
executionRequest.setRestUrl("https://testops.katalon.io/api/v1/executions/1/link-release")
executionRequest.setRestRequestMethod("POST")
addAuthHeader(executionRequest)
addContentTypeHeader(executionRequest)
def executionPayload = '{"id": 1, "projectId": 1, "releaseId": 1}'
executionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionPayload)))
def executionResponse = WSBuiltInKeywords.sendRequest(executionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionResponse, 200)

// Step 4: Create a new ExecutionTestResultResource
def executionTestResultRequest = new RequestObject()
executionTestResultRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/1/incidents")
executionTestResultRequest.setRestRequestMethod("POST")
addAuthHeader(executionTestResultRequest)
addContentTypeHeader(executionTestResultRequest)
def executionTestResultPayload = '{"id": "1", "testCase": "TestCase__unique__", "execution": "Execution__unique__", "platform": "Platform__unique__", "status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T00:01:00Z", "duration": 60, "testSuite": "TestSuite__unique__", "executionTestSuite": "ExecutionTestSuite__unique__", "incidents": []}'
executionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionTestResultPayload)))
def executionTestResultResponse = WSBuiltInKeywords.sendRequest(executionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionTestResultResponse, 200)

// Step 5: Update an Execution Test Result to a Task
def updateRequest = new RequestObject()
updateRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/1/incidents")
updateRequest.setRestRequestMethod("PUT")
addAuthHeader(updateRequest)
addContentTypeHeader(updateRequest)
def updatePayload = '{"id": "1", "incidentId": 1, "projectId": 1, "incidentOrder": 1, "executionTestResultId": 1}'
updateRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatePayload)))
def updateResponse = WSBuiltInKeywords.sendRequest(updateRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
