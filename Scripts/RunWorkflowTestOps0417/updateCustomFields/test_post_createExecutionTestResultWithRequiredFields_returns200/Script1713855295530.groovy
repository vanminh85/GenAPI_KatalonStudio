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

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"name": "Project__' + uuid + '__", "teamId": 1}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def executionRequest = new RequestObject()
executionRequest.setRestUrl("https://testops.katalon.io/api/v1/executions")
executionRequest.setRestRequestMethod("POST")
addAuthHeader(executionRequest)
addContentTypeHeader(executionRequest)
def executionPayload = '{"projectId": 1}'
executionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionPayload)))
def executionResponse = WSBuiltInKeywords.sendRequest(executionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionResponse, 200)

def executionTestResultRequest = new RequestObject()
executionTestResultRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/1/custom-fields")
executionTestResultRequest.setRestRequestMethod("POST")
addAuthHeader(executionTestResultRequest)
addContentTypeHeader(executionTestResultRequest)
def executionTestResultPayload = '{"testCase": "TestCase__' + uuid + '__", "execution": "Execution__' + uuid + '__", "platform": "Platform__' + uuid + '__", "logId": 1, "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "testSuite": "TestSuite__' + uuid + '__", "executionTestSuite": "ExecutionTestSuite__' + uuid + '__", "profile": "Profile__' + uuid + '__", "hasComment": true, "failedTestResultCategory": "APPLICATION", "totalTestObject": 10, "totalDefects": 0, "totalAssertion": 5, "passedAssertion": 3, "failedAssertion": 2, "lastRetryTestId": 1, "lastChangedBy": "User__' + uuid + '__"}'
executionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionTestResultPayload)))
def executionTestResultResponse = WSBuiltInKeywords.sendRequest(executionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionTestResultResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

