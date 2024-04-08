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

// Step 1: Create necessary data for the account creation request
account_payload = '{"code": "test_account__unique__", "email": "test@example.com", "first_name": "John", "last_name": "Doe"}'

// Step 2: Send a POST request to /accounts with an invalid site ID
request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(account_payload)))
request.setRestUrl("https://v3.recurly.com/accounts?site_id=invalid_site_id")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

// Send the request and get the response
response = WSBuiltInKeywords.sendRequest(request)

// Step 3: Verify that the response status code is 404
WSBuiltInKeywords.verifyResponseStatusCode(response, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
