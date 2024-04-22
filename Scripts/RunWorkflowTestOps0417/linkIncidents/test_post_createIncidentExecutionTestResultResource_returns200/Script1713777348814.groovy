import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
	if (authToken) {
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new Team
teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamPayload = '{"id": 1, "name": "Team__unique__", "role": "OWNER", "organizationId": 1}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 2: Create a new Project
projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectPayload = '{"id": 1, "name": "Project__unique__", "teamId": 1, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 3: Create a new ExecutionResource
executionRequest = new RequestObject()
executionRequest.setRestUrl("https://testops.katalon.io/api/v1/executions/1/link-release")
executionRequest.setRestRequestMethod("POST")
addAuthHeader(executionRequest)
addContentTypeHeader(executionRequest)
executionPayload = '{"id": 1, "projectId": 1, "releaseId": 1}'
executionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionPayload)))
executionResponse = WSBuiltInKeywords.sendRequest(executionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionResponse, 200)

// Step 4: Create a new ExecutionTestResultResource
executionTestResultRequest = new RequestObject()
executionTestResultRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/1/incidents")
executionTestResultRequest.setRestRequestMethod("POST")
addAuthHeader(executionTestResultRequest)
addContentTypeHeader(executionTestResultRequest)
executionTestResultPayload = '{"id": "1", "testCase": "TestCase__unique__", "execution": "Execution__unique__", "platform": "Platform__unique__", "status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "duration": 3600, "testSuite": "TestSuite__unique__", "executionTestSuite": "ExecutionTestSuite__unique__", "incidents": []}'
executionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionTestResultPayload)))
executionTestResultResponse = WSBuiltInKeywords.sendRequest(executionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionTestResultResponse, 200)

// Step 5: Link an Execution Test Result to a Task
linkRequest = new RequestObject()
linkRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/1/incidents")
linkRequest.setRestRequestMethod("POST")
addAuthHeader(linkRequest)
addContentTypeHeader(linkRequest)
linkPayload = '{"id": "1", "incidentId": 1, "projectId": 1, "incidentOrder": 1, "executionTestResultId": 1}'
linkRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(linkPayload)))
linkResponse = WSBuiltInKeywords.sendRequest(linkRequest)
WSBuiltInKeywords.verifyResponseStatusCode(linkResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

