import internal.GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
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

def account_payload = new RequestObject()
account_payload.setRestUrl("https://v3.recurly.com/accounts")
account_payload.setRestRequestMethod("POST")
addAuthHeader(account_payload)
addContentTypeHeader(account_payload)
account_payload.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"code": "test_account__unique__"}')))

def account_response = WSBuiltInKeywords.sendRequest(account_payload)
WSBuiltInKeywords.verifyResponseStatusCode(account_response, 201)
def account_id = (new JsonSlurper()).parseText(account_response.getResponseText())["id"]

def account_update_payload = new RequestObject()
account_update_payload.setRestUrl("https://v3.recurly.com/accounts/" + account_id)
account_update_payload.setRestRequestMethod("PUT")
addAuthHeader(account_update_payload)
addContentTypeHeader(account_update_payload)
account_update_payload.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"first_name": "John", "last_name": "Doe", "email": "john.doe@example.com"}')))

def account_update_response = WSBuiltInKeywords.sendRequest(account_update_payload)
WSBuiltInKeywords.verifyResponseStatusCode(account_update_response, 200)

def billing_info_payload = new RequestObject()
billing_info_payload.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/billing_infos")
billing_info_payload.setRestRequestMethod("POST")
addAuthHeader(billing_info_payload)
addContentTypeHeader(billing_info_payload)
billing_info_payload.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"token_id": "token123", "first_name": "John", "last_name": "Doe", "number": "4111111111111111", "month": "12", "year": "2023"}')))

def billing_info_response = WSBuiltInKeywords.sendRequest(billing_info_payload)
WSBuiltInKeywords.verifyResponseStatusCode(billing_info_response, 201)

WSBuiltInKeywords.verifyResponseStatusCode(billing_info_response, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}