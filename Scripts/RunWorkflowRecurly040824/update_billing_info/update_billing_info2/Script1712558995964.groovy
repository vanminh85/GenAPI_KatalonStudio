import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

// Import GlobalVariable
import internal.GlobalVariable

// Define addAuthHeader function
def addAuthHeader(request) {
	def authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		def auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

// Define addContentTypeHeader function
def addContentTypeHeader(request) {
	def content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

// Define uuid variable
uuid = UUID.randomUUID().toString()

// Step 1: Create a new account
def account_payload = JsonOutput.toJson([
	"code": "test_account__unique__"
])
def account_request = new RequestObject()
account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(account_payload)))
account_request.setRestUrl("https://v3.recurly.com/accounts")
account_request.setRestRequestMethod("POST")
addAuthHeader(account_request)
addContentTypeHeader(account_request)
def account_response = WSBuiltInKeywords.sendRequest(account_request)
WSBuiltInKeywords.verifyResponseStatusCode(account_response, 201)
def account_id = (new JsonSlurper()).parseText(account_response.getResponseText())["id"]

// Step 2: Set the necessary fields for the account
def account_update_payload = JsonOutput.toJson([
	"first_name": "John",
	"last_name": "Doe",
	"email": "john.doe@example.com"
])
def account_update_request = new RequestObject()
account_update_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(account_update_payload)))
account_update_request.setRestUrl("https://v3.recurly.com/accounts/" + account_id)
account_update_request.setRestRequestMethod("PUT")
addAuthHeader(account_update_request)
addContentTypeHeader(account_update_request)
def account_update_response = WSBuiltInKeywords.sendRequest(account_update_request)
WSBuiltInKeywords.verifyResponseStatusCode(account_update_response, 200)

// Step 3: Call the API POST /accounts/{account_id}/billing_info with an invalid account ID
def invalid_account_id = "invalid_account_id"
def billing_info_payload = JsonOutput.toJson([
	"token_id": "token_id",
	"first_name": "John",
	"last_name": "Doe",
	"number": "4111111111111111",
	"month": "12",
	"year": "2023",
	"cvv": "123"
])
def billing_info_request = new RequestObject()
billing_info_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(billing_info_payload)))
billing_info_request.setRestUrl("https://v3.recurly.com/accounts/" + invalid_account_id + "/billing_info")
billing_info_request.setRestRequestMethod("POST")
addAuthHeader(billing_info_request)
addContentTypeHeader(billing_info_request)
def billing_info_response = WSBuiltInKeywords.sendRequest(billing_info_request)

// Step 4: Verify that the response status code is 404
WSBuiltInKeywords.verifyResponseStatusCode(billing_info_response, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

