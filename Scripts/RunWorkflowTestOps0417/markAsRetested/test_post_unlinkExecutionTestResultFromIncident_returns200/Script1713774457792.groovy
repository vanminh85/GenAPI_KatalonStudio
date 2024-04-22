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

def execution_test_result_payload = '{"status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "duration": 3600, "totalTests": 10, "totalPassedTests": 10, "totalFailedTests": 0, "totalErrorTests": 0, "totalIncompleteTests": 0, "totalSkippedTests": 0, "totalDiffTests": 0, "totalDiffPassedTests": 0, "totalDiffFailedTests": 0, "totalDiffErrorTests": 0, "totalDiffIncompleteTests": 0, "executionId": 1, "executionOrder": 1, "testCaseId": 1, "projectId": 1}'

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(execution_test_result_payload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/test-results/__unique__/mask-as-retested")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def execution_test_result_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

def unlink_payload = '{"id": "__unique__"}'

def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(unlink_payload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/test-results/${execution_test_result_id}/incidents")
request2.setRestRequestMethod("DELETE")
addAuthHeader(request2)
addContentTypeHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def request3 = new RequestObject()
request3.setRestUrl("https://testops.katalon.io/api/v1/test-results/${execution_test_result_id}/incidents")
request3.setRestRequestMethod("GET")
addAuthHeader(request3)
def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

