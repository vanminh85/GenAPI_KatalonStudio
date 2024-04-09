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
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
def createAccountPayload = '{"code": "test_account__unique__", "first_name": "John", "last_name": "Doe", "email": "john.doe@example.com"}'
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))
def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)

if (createAccountResponse.getStatusCode() == 201) {
	def account_id = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]
	
	// Step 2: Create a new billing info
	def createBillingInfoRequest = new RequestObject()
	createBillingInfoRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/billing_infos")
	createBillingInfoRequest.setRestRequestMethod("POST")
	addAuthHeader(createBillingInfoRequest)
	addContentTypeHeader(createBillingInfoRequest)
	def createBillingInfoPayload = '{"first_name": "John", "last_name": "Doe", "address": {"phone": "1234567890", "street1": "123 Main St", "city": "San Francisco", "region": "CA", "postal_code": "94105", "country": "US"}, "payment_method": {"object": "credit_card", "card_type": "Visa", "first_six": "411111", "last_four": "1111", "exp_month": 12, "exp_year": 2023}, "primary_payment_method": true, "backup_payment_method": false}'
	createBillingInfoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createBillingInfoPayload)))
	def createBillingInfoResponse = WSBuiltInKeywords.sendRequest(createBillingInfoRequest)
	WSBuiltInKeywords.verifyResponseStatusCode(createBillingInfoResponse, 200)

	if (createBillingInfoResponse.getStatusCode() == 200) {
		def billing_info_id = new JsonSlurper().parseText(createBillingInfoResponse.getResponseText())["id"]
		
		// Step 3: Update the billing info
		def updateBillingInfoRequest = new RequestObject()
		updateBillingInfoRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/billing_info")
		updateBillingInfoRequest.setRestRequestMethod("PUT")
		addAuthHeader(updateBillingInfoRequest)
		addContentTypeHeader(updateBillingInfoRequest)
		def updateBillingInfoPayload = '{"first_name": "Jane", "last_name": "Smith"}'
		updateBillingInfoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateBillingInfoPayload)))
		def updateBillingInfoResponse = WSBuiltInKeywords.sendRequest(updateBillingInfoRequest)
		WSBuiltInKeywords.verifyResponseStatusCode(updateBillingInfoResponse, 200)

		// Step 4: Verify response status code is 200
		println("Step 4 - Verify Status Code is 200: ${updateBillingInfoResponse.getStatusCode() == 200}")
	} else {
		println("Step 2 - Create Billing Info Failed")
	}
} else {
	println("Step 1 - Create Account Failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}