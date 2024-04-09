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
def payload_create_account = '{"code": "test_account__unique__"}'
def request_create_account = new RequestObject()
request_create_account.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_create_account)))
request_create_account.setRestUrl("https://v3.recurly.com/accounts")
request_create_account.setRestRequestMethod("POST")
addAuthHeader(request_create_account)
addContentTypeHeader(request_create_account)
def response_create_account = WSBuiltInKeywords.sendRequest(request_create_account)
def account_id = new JsonSlurper().parseText(response_create_account.getResponseText())["id"]

// Step 2: Extract the 'id' from the response
// Already extracted in Step 1

// Step 3: Send a POST request to /accounts/{account_id}/reactivate
def request_reactivate_account = new RequestObject()
request_reactivate_account.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/reactivate")
request_reactivate_account.setRestRequestMethod("POST")
addAuthHeader(request_reactivate_account)
def response_reactivate_account = WSBuiltInKeywords.sendRequest(request_reactivate_account)

// Step 4: Verify that the response status code is 200
def isTestPassed = WSBuiltInKeywords.verifyResponseStatusCode(response_reactivate_account, 200)
if (isTestPassed) {
	println("Test Passed")
} else {
	println("Test Failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}