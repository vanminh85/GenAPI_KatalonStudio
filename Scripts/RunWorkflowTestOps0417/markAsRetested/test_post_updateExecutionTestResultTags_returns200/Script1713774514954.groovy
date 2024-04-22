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
postRequest = new RequestObject()
postRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/{id}/mask-as-retested")
postRequest.setRestRequestMethod("POST")
addAuthHeader(postRequest)
addContentTypeHeader(postRequest)

postPayload = '{"status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "duration": 3600, "totalTests": 10, "totalPassedTests": 10, "totalFailedTests": 0, "totalErrorTests": 0, "totalIncompleteTests": 0, "totalSkippedTests": 0, "totalDiffTests": 0, "totalDiffPassedTests": 0, "totalDiffFailedTests": 0, "totalDiffErrorTests": 0, "totalDiffIncompleteTests": 0, "executionId": 1, "executionOrder": 1, "testCaseId": 1, "projectId": 1}'
postRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(postPayload)))

postResponse = WSBuiltInKeywords.sendRequest(postRequest)
WSBuiltInKeywords.verifyResponseStatusCode(postResponse, 200)

// Step 2: Update the tags of the ExecutionTestResultResource
putRequest = new RequestObject()
putRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/{id}/tags")
putRequest.setRestRequestMethod("PUT")
addAuthHeader(putRequest)
addContentTypeHeader(putRequest)

putPayload = '{"id": "tag1__unique__"}'
putRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(putPayload)))

putResponse = WSBuiltInKeywords.sendRequest(putRequest)
WSBuiltInKeywords.verifyResponseStatusCode(putResponse, 200)

// Step 3: Verify that the response status code is 200
if (putResponse.getStatusCode() == 200) {
	println("Step 3 - Verification: Passed")
} else {
	println("Step 3 - Verification: Failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

