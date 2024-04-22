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
def teamPayload = '{"name": "TeamName__unique__", "role": "OWNER", "organizationId": 1}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 2: Create a new Project
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']
def projectPayload = '{"name": "ProjectName__unique__", "teamId": ' + teamId + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true}'
def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 3: Create a new ExecutionResource
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']
def executionPayload = '{"status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "duration": 3600, "totalTests": 10, "totalPassedTests": 10, "totalFailedTests": 0, "totalErrorTests": 0, "totalIncompleteTests": 0, "totalSkippedTests": 0, "totalDiffTests": 0, "totalDiffPassedTests": 0, "totalDiffFailedTests": 0, "totalDiffErrorTests": 0, "projectId": ' + projectId + ', "executionStage": "COMPLETED", "webUrl": "https://example.com", "hasComment": false, "user": "User__unique__", "sessionId": "Session__unique__", "buildLabel": "BuildLabel__unique__", "buildUrl": "https://build.example.com", "type": "KATALON"}'
def executionRequest = new RequestObject()
executionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionPayload)))
executionRequest.setRestUrl("https://testops.katalon.io/api/v1/executions")
executionRequest.setRestRequestMethod("POST")
addAuthHeader(executionRequest)
addContentTypeHeader(executionRequest)
def executionResponse = WSBuiltInKeywords.sendRequest(executionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionResponse, 200)

// Step 4: Create a new ExecutionTestResultResource
def executionId = new JsonSlurper().parseText(executionResponse.getResponseText())['id']
def executionTestResultPayload = '{"incidentId": 1, "projectId": ' + projectId + ', "incidentOrder": 1, "executionTestResultId": 1}'
def executionTestResultRequest = new RequestObject()
executionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionTestResultPayload)))
executionTestResultRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/" + executionId + "/incidents")
executionTestResultRequest.setRestRequestMethod("POST")
addAuthHeader(executionTestResultRequest)
addContentTypeHeader(executionTestResultRequest)
def executionTestResultResponse = WSBuiltInKeywords.sendRequest(executionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionTestResultResponse, 200)

// Step 5: Update the created IncidentExecutionTestResultResource
def updatedExecutionTestResultPayload = '{"incidentId": 2, "projectId": ' + projectId + ', "incidentOrder": 2, "executionTestResultId": 2}'
def updatedExecutionTestResultRequest = new RequestObject()
updatedExecutionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedExecutionTestResultPayload)))
updatedExecutionTestResultRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/" + executionId + "/incidents")
updatedExecutionTestResultRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedExecutionTestResultRequest)
addContentTypeHeader(updatedExecutionTestResultRequest)
def updatedExecutionTestResultResponse = WSBuiltInKeywords.sendRequest(updatedExecutionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedExecutionTestResultResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}


