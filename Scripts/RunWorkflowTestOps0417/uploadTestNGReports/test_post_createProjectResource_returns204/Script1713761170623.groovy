import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new ProjectResource
def project_resource_data = '''
{
    "name": "ProjectName__unique__",
    "teamId": 123,
    "team": "TeamName__unique__",
    "timezone": "UTC",
    "status": "ACTIVE",
    "canAutoIntegrate": true,
    "sampleProject": true
}
'''

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_resource_data)))
request1.setRestUrl("https://testops.katalon.io/api/v1/projects")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)

def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)
def project_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 2: Send a POST request to /api/v1/testng/test-reports with the created ProjectResource data
def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_resource_data)))
request2.setRestUrl("https://testops.katalon.io/api/v1/testng/test-reports")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)

def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 204)

// Step 3: Verify the response status code is 204
WSBuiltInKeywords.verifyResponseStatusCode(response2, 204)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

