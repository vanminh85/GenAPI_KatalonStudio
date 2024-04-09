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

def accountPayload = '{"code": "test_account__unique__", "first_name": "John", "last_name": "Doe", "email": "john.doe@example.com"}'
def billingInfoPayload = '{"first_name": "John", "last_name": "Doe", "address": "123 Main St", "payment_method": "credit_card", "primary_payment_method": true, "backup_payment_method": false}'

def accountRequest = new RequestObject()
accountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountPayload)))
accountRequest.setRestUrl("https://v3.recurly.com/accounts")
accountRequest.setRestRequestMethod("POST")
addAuthHeader(accountRequest)
addContentTypeHeader(accountRequest)
def accountResponse = WSBuiltInKeywords.sendRequest(accountRequest)
def accountId = new JsonSlurper().parseText(accountResponse.getResponseText())["id"]

def billingInfoRequest = new RequestObject()
billingInfoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(billingInfoPayload)))
billingInfoRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/billing_infos")
billingInfoRequest.setRestRequestMethod("POST")
addAuthHeader(billingInfoRequest)
addContentTypeHeader(billingInfoRequest)
WSBuiltInKeywords.sendRequest(billingInfoRequest)

def deleteBillingInfoRequest = new RequestObject()
deleteBillingInfoRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/billing_info")
deleteBillingInfoRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteBillingInfoRequest)
WSBuiltInKeywords.sendRequest(deleteBillingInfoRequest)

def verifyResponse = new RequestObject()
verifyResponse.setRestRequestMethod("GET")
WSBuiltInKeywords.verifyResponseStatusCode(verifyResponse, 204)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}