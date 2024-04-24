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

def payload = '''
{
    "name": "TestProject__unique__",
    "description": "Test project description",
    "defaultTestProject": true,
    "uploadFileId": 123,
    "projectId": 456,
    "teamId": 789,
    "createdAt": "2022-01-01T00:00:00Z",
    "latestJob": {"id": 1, "name": "Latest Job"},
    "uploadFileName": "test_file.txt",
    "type": "KS",
    "gitRepository": {"id": 1, "testProjectId": 1, "name": "TestRepo__unique__", "repository": "https://github.com/testrepo", "branch": "main", "username": "user", "password": "pass", "accessKeyId": "access_key", "secretAccessKey": "secret_key", "projectId": 1, "teamId": 1, "createdAt": "2022-01-01T00:00:00Z", "updatedAt": "2022-01-01T00:00:00Z", "description": "Test repository description", "vcsType": "GITHUB", "shouldMergeTestResultsForNewScriptRepo": true},
    "testSuiteCollections": [],
    "dirty": false,
    "autonomous": true
}
'''

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def test_project_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

def request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${test_project_id}")
request2.setRestRequestMethod("GET")
addAuthHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}


