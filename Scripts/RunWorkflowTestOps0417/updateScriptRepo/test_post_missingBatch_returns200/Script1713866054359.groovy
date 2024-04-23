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
testProjectPayload = '{"name": "TestProject__unique__", "projectId": 123, "teamId": 456}'
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

if (response1.getStatusCode() == 200) {
	testProjectId = new JsonSlurper().parseText(response1.getResponseText())['id']

	// Step 2: Update script repository for the Test Project
	updateScriptPayload = '{"folderPath": "Scripts__unique__", "fileName": "script.py", "uploadedPath": "path/to/uploaded/script.py"}'
	request2 = new RequestObject()
	request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateScriptPayload)))
	request2.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + testProjectId + "/update-script-repo")
	request2.setRestRequestMethod("POST")
	addAuthHeader(request2)
	addContentTypeHeader(request2)
	response2 = WSBuiltInKeywords.sendRequest(request2)
	WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

	// Step 3: Verify response status code is not 200
	if (response2.getStatusCode() != 200) {
		println("Step 3 - Response status code is not 200 as expected.")
	} else {
		println("Step 3 - Response status code is 200, which is unexpected.")
	}
} else {
	println("Step 2 and 3 are skipped as Step 1 did not return a 200 status code.")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

