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

def test_case_data = '''
{
    "name": "Test Case Name__unique__",
    "path": "Test Case Path__unique__",
    "previousStatus": "PASSED",
    "testModuleId": 123,
    "webUrl": "https://example.com",
    "description": "Test Case Description",
    "project": {"id": 456},
    "lastExecutionTestCase": {"id": 789},
    "externalIssues": [],
    "customFieldOptions": [],
    "type": "TEST_CASE",
    "averageDuration": 1.5,
    "maxDuration": 60,
    "minDuration": 30,
    "flakiness": 0.1,
    "platformStatistics": {},
    "maintainer": {"id": 987},
    "testResultAssertion": {"id": 654},
    "updatedAt": "2022-01-01T00:00:00Z",
    "createdAt": "2022-01-01T00:00:00Z",
    "testProject": {"id": 321},
    "numberOfExecutions": 10,
    "testType": "G4_TEST_CASE",
    "urlId": "test-case-url"
}
'''

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(test_case_data)))
request1.setRestUrl("${GlobalVariable.base_url}/api/v1/test-cases/update")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(test_case_data)))
request2.setRestUrl("${GlobalVariable.base_url}/api/v1/testng/test-reports")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 204)

if (response2.getStatusCode() == 204) {
	println("Step 3 - Response status code is 204 - Test Passed")
} else {
	println("Step 3 - Response status code is not 204 - Test Failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

