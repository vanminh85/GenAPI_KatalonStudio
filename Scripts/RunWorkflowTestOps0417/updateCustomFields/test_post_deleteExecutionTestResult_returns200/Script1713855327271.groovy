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

// Step 1: Create a new Execution Test Result
def createExecutionTestResultRequest = new RequestObject()
createExecutionTestResultRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-results/${id}/custom-fields")
createExecutionTestResultRequest.setRestRequestMethod("POST")
addAuthHeader(createExecutionTestResultRequest)
addContentTypeHeader(createExecutionTestResultRequest)

def payload = '{"testCase": "testCase__unique__", "execution": "execution__unique__", "platform": "platform__unique__", "logId": "logId__unique__", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "testSuite": "testSuite__unique__", "executionTestSuite": "executionTestSuite__unique__", "profile": "profile__unique__", "hasComment": true, "failedTestResultCategory": "APPLICATION", "totalTestObject": 10, "totalDefects": 0, "totalAssertion": 5, "passedAssertion": 3, "failedAssertion": 2, "lastRetryTestId": 0, "lastChangedBy": "lastChangedBy__unique__"}'
createExecutionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))

def createExecutionTestResultResponse = WSBuiltInKeywords.sendRequest(createExecutionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createExecutionTestResultResponse, 200)

// Step 2: Delete the created Execution Test Result
def deleteExecutionTestResultRequest = new RequestObject()
deleteExecutionTestResultRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-results/${id}/custom-fields")
deleteExecutionTestResultRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteExecutionTestResultRequest)

def deleteExecutionTestResultResponse = WSBuiltInKeywords.sendRequest(deleteExecutionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteExecutionTestResultResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

