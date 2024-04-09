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

def createAccountRequest = new RequestObject()
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"code":"test_account__unique__","first_name":"John","last_name":"Doe","email":"johndoe@example.com"}')))
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)

if (WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)) {
	def account_id = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]
	
	def getBalanceRequest = new RequestObject()
	getBalanceRequest.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/balance")
	getBalanceRequest.setRestRequestMethod("POST")
	addAuthHeader(getBalanceRequest)
	def getBalanceResponse = WSBuiltInKeywords.sendRequest(getBalanceRequest)
	
	if (WSBuiltInKeywords.verifyResponseStatusCode(getBalanceResponse, 422)) {
		println("Step 3 - Response Status Code Verified: 422 due to missing billing information")
	}
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}