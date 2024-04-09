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

// Step 1: Create a new account
def accountRequest = new RequestObject()
accountRequest.setRestUrl("https://v3.recurly.com/accounts")
accountRequest.setRestRequestMethod("POST")
addAuthHeader(accountRequest)
addContentTypeHeader(accountRequest)

def accountPayload = '{"code": "test_account__unique__", "email": "test@example.com", "first_name": "John", "last_name": "Doe"}'
accountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountPayload)))

def accountResponse = WSBuiltInKeywords.sendRequest(accountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(accountResponse, 201)

def accountId = new JsonSlurper().parseText(accountResponse.getResponseText())["id"]

// Step 2: Create account acquisition data
def acquisitionRequest = new RequestObject()
acquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/acquisition")
acquisitionRequest.setRestRequestMethod("POST")
addAuthHeader(acquisitionRequest)
addContentTypeHeader(acquisitionRequest)

def acquisitionPayload = '{"cost": {"currency": "USD", "amount": 100.0}, "channel": "direct_traffic", "subchannel": "online", "campaign": "summer_sale"}'
acquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(acquisitionPayload)))

def acquisitionResponse = WSBuiltInKeywords.sendRequest(acquisitionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(acquisitionResponse, 201)

// Step 3: Update account acquisition data
def updatedAcquisitionRequest = new RequestObject()
updatedAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/acquisition")
updatedAcquisitionRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedAcquisitionRequest)
addContentTypeHeader(updatedAcquisitionRequest)

def updatedAcquisitionPayload = '{"cost": {"currency": "USD", "amount": 150.0}, "channel": "paid_search", "subchannel": "google_ads", "campaign": "fall_promo"}'
updatedAcquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedAcquisitionPayload)))

def updatedAcquisitionResponse = WSBuiltInKeywords.sendRequest(updatedAcquisitionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedAcquisitionResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}