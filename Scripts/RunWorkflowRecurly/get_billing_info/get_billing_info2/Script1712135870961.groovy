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

// Step 1: Create a new account
def createAccountRequest = new RequestObject()
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addContentTypeHeader(createAccountRequest)

def createAccountPayload = JsonOutput.toJson([
	"code": "test_account",
	"email": "test@example.com",
	"first_name": "John",
	"last_name": "Doe"
])
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))
addAuthHeader(createAccountRequest)

def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)
def account_id = (new JsonSlurper()).parseText(createAccountResponse.getResponseText())["id"]

// Step 2: Create invalid billing information
def createBillingInfoRequest = new RequestObject()
createBillingInfoRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/billing_infos")
createBillingInfoRequest.setRestRequestMethod("POST")
addContentTypeHeader(createBillingInfoRequest)

def createBillingInfoPayload = JsonOutput.toJson([
	"first_name": "John",
	"last_name": "Doe",
	"number": "4111111111111111",
	"month": "12",
	"year": "2022",
	"cvv": "123"
])
createBillingInfoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createBillingInfoPayload)))
addAuthHeader(createBillingInfoRequest)

def createBillingInfoResponse = WSBuiltInKeywords.sendRequest(createBillingInfoRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createBillingInfoResponse, 400)

// Step 3: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(createBillingInfoResponse, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
