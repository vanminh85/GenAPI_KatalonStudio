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

// Step 1: Create a new Test Project
def testProjectPayload = '{"name": "TestProject__' + uuid + '__", "projectId": 123, "teamId": 456}'
def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Extract testProjectId from the response for Step 2
def testProjectId = new JsonSlurper().parseText(response1.getResponseText()).id

// Step 2: Update script repository for the Test Project
def updateScriptPayload = '{"batch": "batch__' + uuid + '__", "fileName": "script.py", "uploadedPath": "/path/to/upload"}'
def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateScriptPayload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + testProjectId + "/update-script-repo")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3: Verify the response status code is not 200
if (response2.getStatusCode() != 200) {
	println("Step 3 - Response status code is not 200 as expected")
} else {
	println("Step 3 - Response status code is 200, which is unexpected")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

