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

// Step 1: Retrieve an existing Test Suite ID
def request1 = new RequestObject()
request1.setRestUrl("https://testops.katalon.io/api/v1/test-suites/123")
request1.setRestRequestMethod("GET")
addAuthHeader(request1)
WSBuiltInKeywords.sendRequest(request1)

// Step 2: Update the Test Suite
def payload = '{"name": "Updated Test Suite Name", "path": "Updated Test Suite Path", "testResults": [], "project": {"id": 456}, "alias": "Updated Alias", "lastExecutionTestSuite": {"id": 789}, "updatedAt": "2022-01-01T00:00:00Z", "testCases": [], "testProject": {"id": 101}, "createdAt": "2022-01-01T00:00:00Z", "user": {"id": 202}, "type": "KATALON_STUDIO", "testFolder": {"id": 303}, "urlId": "updated-url-id"}'

def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/test-suites/123/edit")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
WSBuiltInKeywords.sendRequest(request2)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

