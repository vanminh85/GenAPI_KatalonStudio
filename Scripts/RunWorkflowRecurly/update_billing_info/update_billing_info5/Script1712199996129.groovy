import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.util.UUID

import static org.assertj.core.api.Assertions.*

// Step 1: Create a new account
def account_payload = [
	"code": "test_account"
]

def account_request = new RequestObject()
account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(account_payload))))
account_request.setRestUrl("https://v3.recurly.com/accounts")
account_request.setRestRequestMethod("POST")
addContentTypeHeader(account_request)
addAuthHeader(account_request)

def account_response = WSBuiltInKeywords.sendRequest(account_request)
WSBuiltInKeywords.verifyResponseStatusCode(account_response, 201)
def account_id = JsonSlurper().parseText(account_response.getResponseText())["id"]

// Step 2: Set the necessary fields for the account
def billing_info_payload = [
	"first_name": "John",
	"last_name": "Doe",
	"number": "4111111111111111",
	"month": "12",
	"year": "2023"
]

def billing_info_request = new RequestObject()
billing_info_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(billing_info_payload))))
billing_info_request.setRestUrl("https://v3.recurly.com/accounts/${account_id}/billing_info")
billing_info_request.setRestRequestMethod("POST")
addContentTypeHeader(billing_info_request)
addAuthHeader(billing_info_request)

// Step 3: Call the API POST /accounts/{account_id}/billing_info with an invalid site ID
def response = WSBuiltInKeywords.sendRequest(billing_info_request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 404)

// Step 4: Verify that the response status code is 404
assertThat(response.getStatusCode()).isEqualTo(404)

def addAuthHeader(request) {
	def authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
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

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
