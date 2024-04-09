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
addAuthHeader(request1)
addContentTypeHeader(request1)
request1.setRestUrl("https://v3.recurly.com/accounts")
request1.setRestRequestMethod("POST")
payload1 = '{"code": "test_account__unique__","acquisition": {"cost": {"currency": "USD","amount": 100.0},"channel": "email","subchannel": "newsletter","campaign": "summer_sale"},"external_accounts": []}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 201)
account_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 4: Create a new account acquisition
request2 = new RequestObject()
addAuthHeader(request2)
addContentTypeHeader(request2)
request2.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
request2.setRestRequestMethod("POST")
payload2 = '{"cost": {"currency": "USD","amount": 150.0},"channel": "social_media","subchannel": "facebook","campaign": "holiday_promo"}'
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload2)))
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 201)

// Step 6: Update the account acquisition data
request3 = new RequestObject()
addAuthHeader(request3)
addContentTypeHeader(request3)
request3.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
request3.setRestRequestMethod("PUT")
updated_payload = '{"cost": {"currency": "USD","amount": 200.0},"channel": "advertising","subchannel": "google_ads","campaign": "winter_sale"}'
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updated_payload)))
response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
