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

// Step 1: Create a new account
request1 = new RequestObject()
request1.setRestUrl("https://v3.recurly.com/accounts")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
payload1 = '{"code": "test_account__unique__"}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))
response1 = WSBuiltInKeywords.sendRequest(request1)
account_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 2: Use the newly created account's ID in the path parameter for the next request

// Step 3: Create a new account acquisition
request2 = new RequestObject()
request2.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
payload2 = '{"cost": {"currency": "USD", "amount": 100.0}, "channel": "email", "subchannel": "marketing", "campaign": "summer_sale"}'
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload2)))
response2 = WSBuiltInKeywords.sendRequest(request2)

// Step 4: Update the account acquisition
request3 = new RequestObject()
request3.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
request3.setRestRequestMethod("PUT")
addAuthHeader(request3)
addContentTypeHeader(request3)
updated_payload = '{"cost": {"currency": "USD", "amount": 150.0}, "channel": "social_media", "subchannel": "advertising", "campaign": "fall_promo"}'
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updated_payload)))
response3 = WSBuiltInKeywords.sendRequest(request3)

// Step 5: Verify that the response status code is 200
assert WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}