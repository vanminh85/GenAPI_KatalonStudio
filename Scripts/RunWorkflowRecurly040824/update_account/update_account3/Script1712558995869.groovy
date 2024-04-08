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
def createAccountPayload = '{"code": "test_account__unique__", "email": "test@example.com", "first_name": "John", "last_name": "Doe"}'
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)
def accountId = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

def deactivateAccountRequest = new RequestObject()
deactivateAccountRequest.setRestUrl("https://v3.recurly.com/accounts/" + accountId)
deactivateAccountRequest.setRestRequestMethod("DELETE")
addAuthHeader(deactivateAccountRequest)
def deactivateResponse = WSBuiltInKeywords.sendRequest(deactivateAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deactivateResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
