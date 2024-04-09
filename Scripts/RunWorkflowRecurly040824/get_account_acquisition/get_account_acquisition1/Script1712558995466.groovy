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
def createAccountPayload = '{"code": "test_account__unique__", "acquisition": {"cost": {"currency": "USD", "amount": 10.0}, "channel": "email"}}'
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
def accountId = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

// Step 3: Create a new account acquisition
def createAccountAcquisitionRequest = new RequestObject()
def createAccountAcquisitionPayload = '{"cost": {"currency": "USD", "amount": 5.0}, "channel": "referral"}'
createAccountAcquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountAcquisitionPayload)))
createAccountAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/acquisition")
createAccountAcquisitionRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountAcquisitionRequest)
addContentTypeHeader(createAccountAcquisitionRequest)
def createAccountAcquisitionResponse = WSBuiltInKeywords.sendRequest(createAccountAcquisitionRequest)

// Step 4: Verify the response status code is 201
WSBuiltInKeywords.verifyResponseStatusCode(createAccountAcquisitionResponse, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}