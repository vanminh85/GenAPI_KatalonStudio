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

def base_url = "https://v3.recurly.com"

// Step 1: Create a new account with missing required fields
def account_payload = [
	"code": "test_account",
	"email": "test@example.com",
	"billing_info": [
		"token_id": "test_token"
	]
]

def account_request = new RequestObject()
account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(account_payload))))
account_request.setRestUrl("${base_url}/accounts")
account_request.setRestRequestMethod("POST")
addAuthHeader(account_request)
addContentTypeHeader(account_request)

def account_response = WSBuiltInKeywords.sendRequest(account_request)
WSBuiltInKeywords.verifyResponseStatusCode(account_response, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
