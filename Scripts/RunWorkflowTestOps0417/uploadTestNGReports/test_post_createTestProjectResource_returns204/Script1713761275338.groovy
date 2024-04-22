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

def testProjectPayload = '''
{
    "name": "TestProject__unique__",
    "description": "Test Description",
    "defaultTestProject": true,
    "uploadFileId": 123,
    "projectId": 456,
    "teamId": 789,
    "createdAt": "2022-01-01T00:00:00Z",
    "latestJob": {"id": 1, "name": "Latest Job"},
    "uploadFileName": "test_file.txt",
    "type": "KS",
    "gitRepository": {"url": "https://github.com/example"},
    "testSuiteCollections": [{"id": 1, "name": "Test Suite Collection"}],
    "dirty": false,
    "autonomous": true
}
'''

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
request.setRestUrl("https://testops.katalon.io/api/v1/testng/test-reports")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 204)

println("Test case executed successfully.")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

