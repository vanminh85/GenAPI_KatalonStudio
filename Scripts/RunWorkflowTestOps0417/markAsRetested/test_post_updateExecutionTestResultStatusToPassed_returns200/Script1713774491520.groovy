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

def payload_step1 = '{"status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "duration": 3600, "totalTests": 10, "totalPassedTests": 10, "totalFailedTests": 0, "totalErrorTests": 0, "totalIncompleteTests": 0, "totalSkippedTests": 0, "totalDiffTests": 0, "totalDiffPassedTests": 0, "totalDiffFailedTests": 0, "totalDiffErrorTests": 0, "totalDiffIncompleteTests": 0, "executionId": 1, "executionOrder": 1, "testCaseId": 1, "projectId": 1}'
def request_step1 = new RequestObject()
request_step1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_step1)))
request_step1.setRestUrl("https://testops.katalon.io/api/v1/test-results/1/mask-as-retested")
request_step1.setRestRequestMethod("POST")
addAuthHeader(request_step1)
addContentTypeHeader(request_step1)
def response_step1 = WSBuiltInKeywords.sendRequest(request_step1)
WSBuiltInKeywords.verifyResponseStatusCode(response_step1, 200)

def payload_step2 = '{"status": "PASSED"}'
def request_step2 = new RequestObject()
request_step2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_step2)))
request_step2.setRestUrl("https://testops.katalon.io/api/v1/test-results/1/mask-as-retested")
request_step2.setRestRequestMethod("PUT")
addAuthHeader(request_step2)
addContentTypeHeader(request_step2)
def response_step2 = WSBuiltInKeywords.sendRequest(request_step2)
WSBuiltInKeywords.verifyResponseStatusCode(response_step2, 200)

println("Verify that the response status code is 200.")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

