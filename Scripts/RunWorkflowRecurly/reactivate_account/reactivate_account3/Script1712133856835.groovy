import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

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

def createAccountPayload = JsonOutput.toJson([
	"code": "test_account__unique__",
	"email": "test@example.com",
	"first_name": "John",
	"last_name": "Doe"
])
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))

def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)

// Step 2: Delete the account
def accountId = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]
def deleteAccountRequest = new RequestObject()
deleteAccountRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}")
deleteAccountRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteAccountRequest)

def deleteAccountResponse = WSBuiltInKeywords.sendRequest(deleteAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteAccountResponse, 204)

// Step 3: Reactivate the account
def reactivateAccountRequest = new RequestObject()
reactivateAccountRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/reactivate")
reactivateAccountRequest.setRestRequestMethod("PUT")
addAuthHeader(reactivateAccountRequest)

def reactivateAccountResponse = WSBuiltInKeywords.sendRequest(reactivateAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(reactivateAccountResponse, 404)

// Step 4: Verify that the response status code is 404
WSBuiltInKeywords.verifyResponseStatusCode(reactivateAccountResponse, 404)

println("Test case executed successfully.")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
