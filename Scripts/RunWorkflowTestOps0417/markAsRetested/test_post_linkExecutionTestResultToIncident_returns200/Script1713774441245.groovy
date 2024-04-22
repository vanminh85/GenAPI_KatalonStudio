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

// Step 1: Create a new ExecutionTestResultResource
def executionTestResultRequest = new RequestObject()
executionTestResultRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/${uuid}/mask-as-retested")
executionTestResultRequest.setRestRequestMethod("POST")
addAuthHeader(executionTestResultRequest)
addContentTypeHeader(executionTestResultRequest)

def executionTestResultPayload = '{"status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "duration": 3600, "totalTests": 10, "totalPassedTests": 10, "totalFailedTests": 0, "totalErrorTests": 0, "totalIncompleteTests": 0, "totalSkippedTests": 0, "totalDiffTests": 0, "totalDiffPassedTests": 0, "totalDiffFailedTests": 0, "totalDiffErrorTests": 0, "totalDiffIncompleteTests": 0, "executionId": 1, "executionOrder": 1, "testCaseId": 1, "projectId": 1}'
executionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionTestResultPayload)))

def executionTestResultResponse = WSBuiltInKeywords.sendRequest(executionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionTestResultResponse, 200)

// Step 2: Link the ExecutionTestResultResource to an Incident
def linkRequest = new RequestObject()
linkRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/${uuid}/incidents")
linkRequest.setRestRequestMethod("POST")
addAuthHeader(linkRequest)
addContentTypeHeader(linkRequest)

def incidentId = "__unique__" // Replace with actual incident ID
def linkPayload = '{"id": "${incidentId}"}'
linkRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(linkPayload)))

def linkResponse = WSBuiltInKeywords.sendRequest(linkRequest)
WSBuiltInKeywords.verifyResponseStatusCode(linkResponse, 200)

// Step 3: Verify the response status code is 200
if (linkResponse.getStatusCode() == 200) {
	println("Test case passed")
} else {
	println("Test case failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

