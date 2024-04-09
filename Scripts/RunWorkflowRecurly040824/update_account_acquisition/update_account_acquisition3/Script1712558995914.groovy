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
def accountPayload = '{"code": "test_account__unique__"}'
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountPayload)))
def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)
def account_id = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

// Step 2: Use the newly created account's ID in the path parameter for the next request

// Step 3: Create a new account acquisition
def createAcquisitionRequest = new RequestObject()
createAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
createAcquisitionRequest.setRestRequestMethod("POST")
addAuthHeader(createAcquisitionRequest)
addContentTypeHeader(createAcquisitionRequest)
def acquisitionPayload = '{"cost": {"currency": "USD", "amount": 100.0}, "channel": "email", "subchannel": "marketing", "campaign": "summer_sale"}'
createAcquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(acquisitionPayload)))
def createAcquisitionResponse = WSBuiltInKeywords.sendRequest(createAcquisitionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAcquisitionResponse, 201)

// Step 4: Remove the account acquisition
def deleteAcquisitionRequest = new RequestObject()
deleteAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/acquisition")
deleteAcquisitionRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteAcquisitionRequest)
def deleteAcquisitionResponse = WSBuiltInKeywords.sendRequest(deleteAcquisitionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteAcquisitionResponse, 204)

// Step 5: Verify that the response status code is 204
assert deleteAcquisitionResponse.getStatusCode() == 204

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}