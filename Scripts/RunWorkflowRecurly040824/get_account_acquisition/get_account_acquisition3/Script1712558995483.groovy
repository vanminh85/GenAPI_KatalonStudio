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
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"code":"test_account__unique__","acquisition":{"cost":{"currency":"USD","amount":10.0},"channel":"email","subchannel":"marketing","campaign":"summer_sale"}}')))
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
def account_id = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

// Step 3: Retrieve the account acquisition data
def retrieveAcquisitionRequest = new RequestObject()
retrieveAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/acquisition")
retrieveAcquisitionRequest.setRestRequestMethod("GET")
addAuthHeader(retrieveAcquisitionRequest)
def retrieveAcquisitionResponse = WSBuiltInKeywords.sendRequest(retrieveAcquisitionRequest)

// Step 4: Verify that the response status code is 404
WSBuiltInKeywords.verifyResponseStatusCode(retrieveAcquisitionResponse, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}