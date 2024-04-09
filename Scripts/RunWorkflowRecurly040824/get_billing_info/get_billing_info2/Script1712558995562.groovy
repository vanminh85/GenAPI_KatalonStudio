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
def createAccountRequest = new RequestObject()
createAccountRequest.setRestUrl("https://https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)

def accountPayload = '{"code": "test_account__unique__", "first_name": "John", "last_name": "Doe", "email": "john.doe@example.com"}'
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountPayload)))

def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
def account_id = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

// Step 2: Create a new billing info with missing required field
def createBillingInfoRequest = new RequestObject()
createBillingInfoRequest.setRestUrl("https://https://v3.recurly.com/accounts/${account_id}/billing_infos")
createBillingInfoRequest.setRestRequestMethod("POST")
addAuthHeader(createBillingInfoRequest)
addContentTypeHeader(createBillingInfoRequest)

def billingInfoPayload = '{"first_name": "John", "last_name": "Doe", "number": "4111111111111111", "month": "12", "year": "2023", "cvv": "123"}'
createBillingInfoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(billingInfoPayload)))

def createBillingInfoResponse = WSBuiltInKeywords.sendRequest(createBillingInfoRequest)

// Step 3: Verify the response status code is 422
def statusCode422 = 422
def isStatusCode422 = WSBuiltInKeywords.verifyResponseStatusCode(createBillingInfoResponse, statusCode422)
assert isStatusCode422

println("Step 1 - Create Account Status Code: " + createAccountResponse.getStatusCode())
println("Step 2 - Create Billing Info Status Code: " + createBillingInfoResponse.getStatusCode())
println("Step 3 - Verify Status Code 422: Passed")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}