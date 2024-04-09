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
def accountPayload = '{"code": "test_account__unique__", "acquisition": {"cost": {"currency": "USD", "amount": 50.0}, "channel": "email", "subchannel": "newsletter", "campaign": "summer_sale"}, "external_accounts": []}'
def accountRequest = new RequestObject()
accountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountPayload)))
accountRequest.setRestUrl("https://v3.recurly.com/accounts")
accountRequest.setRestRequestMethod("POST")
addAuthHeader(accountRequest)
addContentTypeHeader(accountRequest)
def accountResponse = WSBuiltInKeywords.sendRequest(accountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(accountResponse, 201)

def accountId = new JsonSlurper().parseText(accountResponse.getResponseText())["id"]

// Step 2: Create a new account acquisition
def acquisitionPayload = '{"cost": {"currency": "USD", "amount": 100.0}, "channel": "advertising", "subchannel": "social_media", "campaign": "holiday_promo"}'
def acquisitionRequest = new RequestObject()
acquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(acquisitionPayload)))
acquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/acquisition")
acquisitionRequest.setRestRequestMethod("POST")
addAuthHeader(acquisitionRequest)
addContentTypeHeader(acquisitionRequest)
def acquisitionResponse = WSBuiltInKeywords.sendRequest(acquisitionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(acquisitionResponse, 201)

// Step 3: Delete the account acquisition data
def deleteRequest = new RequestObject()
deleteRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/acquisition")
deleteRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteRequest)
def deleteResponse = WSBuiltInKeywords.sendRequest(deleteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteResponse, 204)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
