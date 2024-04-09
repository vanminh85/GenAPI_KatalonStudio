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

def request1 = new RequestObject()
request1.setRestUrl("https://v3.recurly.com/accounts")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def payload1 = '{"code": "test_account__unique__", "first_name": "John", "last_name": "Doe", "email": "john.doe@example.com"}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))
def response1 = WSBuiltInKeywords.sendRequest(request1)
def account_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

def request2 = new RequestObject()
request2.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/billing_infos")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def payload2 = '{"first_name": "John", "last_name": "Doe", "address": {"phone": "1234567890", "street1": "123 Main St", "city": "San Francisco", "region": "CA", "postal_code": "94105", "country": "US"}, "payment_method": {"object": "credit_card", "card_type": "Visa", "first_six": "411111", "last_four": "1111", "exp_month": 12, "exp_year": 2023}, "primary_payment_method": true, "backup_payment_method": false}'
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload2)))
def response2 = WSBuiltInKeywords.sendRequest(request2)
def billing_info_id = new JsonSlurper().parseText(response2.getResponseText())["id"]

def request3 = new RequestObject()
request3.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/billing_info")
request3.setRestRequestMethod("PUT")
addAuthHeader(request3)
addContentTypeHeader(request3)
def payload3 = '{"first_name": "InvalidFirstName"}'
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload3)))
def response3 = WSBuiltInKeywords.sendRequest(request3)

WSBuiltInKeywords.verifyResponseStatusCode(response3, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}