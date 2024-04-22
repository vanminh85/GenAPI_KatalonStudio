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

// Step 1: Create a new User Setting with 'mailWeeklyUpdate' set to true
user_setting_payload = '{"mailJobStart": false, "mailJobEnd": false, "mailExecution": false, "mailExecutionStatus": "mailExecutionStatus__unique__", "mailWeeklyUpdate": true}'

// Step 2: Send a POST request to /api/v1/user-settings with the created User Setting data
request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(user_setting_payload)))
request.setRestUrl("https://testops.katalon.io/api/v1/user-settings")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

response = WSBuiltInKeywords.sendRequest(request)

// Step 3: Verify that the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
