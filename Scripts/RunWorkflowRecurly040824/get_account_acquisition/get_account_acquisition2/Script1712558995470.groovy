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
accountRequest = new RequestObject()
accountRequest.setRestUrl("https://v3.recurly.com/accounts")
accountRequest.setRestRequestMethod("POST")
addAuthHeader(accountRequest)
addContentTypeHeader(accountRequest)

account_payload = '{"code": "test_account__unique__", "acquisition": {"cost": {"currency": "USD", "amount": 100.0}, "channel": "email", "subchannel": "marketing", "campaign": "summer_sale"}, "shipping_addresses": []}'
accountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(account_payload)))

accountResponse = WSBuiltInKeywords.sendRequest(accountRequest)
account_id = new JsonSlurper().parseText(accountResponse.getResponseText())["id"]

// Step 3: Create acquisition data for the account
acquisitionRequest = new RequestObject()
acquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
acquisitionRequest.setRestRequestMethod("POST")
addAuthHeader(acquisitionRequest)
addContentTypeHeader(acquisitionRequest)

acquisition_payload = '{"cost": {"currency": "USD", "amount": 150.0}, "channel": "social_media", "subchannel": "advertising", "campaign": "holiday_promo"}'
acquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(acquisition_payload)))

acquisitionResponse = WSBuiltInKeywords.sendRequest(acquisitionRequest)

// Step 4: Update acquisition data for the account
updatedAcquisitionRequest = new RequestObject()
updatedAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
updatedAcquisitionRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedAcquisitionRequest)
addContentTypeHeader(updatedAcquisitionRequest)

updated_acquisition_payload = '{"cost": {"currency": "USD", "amount": 200.0}, "channel": "referral", "subchannel": "customer_referral", "campaign": "loyalty_program"}'
updatedAcquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updated_acquisition_payload)))

updatedAcquisitionResponse = WSBuiltInKeywords.sendRequest(updatedAcquisitionRequest)

// Step 5: Verify response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(updatedAcquisitionResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
