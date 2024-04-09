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
account_payload = '{"code": "test_account__unique__", "acquisition": {"cost": {"currency": "USD", "amount": 100.0}, "channel": "direct_traffic", "subchannel": "online_ads", "campaign": "summer_sale"}, "shipping_addresses": []}'
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(account_payload)))
request1.setRestUrl("https://v3.recurly.com/accounts")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 201)
account_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 3: Create acquisition data for the account
acquisition_payload = '{"cost": {"currency": "USD", "amount": 50.0}, "channel": "email", "subchannel": "newsletter", "campaign": "fall_promo"}'
request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(acquisition_payload)))
request2.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 4: Delete the acquisition data
request3 = new RequestObject()
request3.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
request3.setRestRequestMethod("DELETE")
addAuthHeader(request3)
response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 204)

// Step 5: Verify the response status code is 204
assert response3.getStatusCode() == 204

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
