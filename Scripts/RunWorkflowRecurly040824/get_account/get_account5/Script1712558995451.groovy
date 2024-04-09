import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new account
account_payload = '{"code": "test_account__unique__", "email": "test@example.com", "first_name": "John", "last_name": "Doe"}'
request_create_account = new RequestObject()
request_create_account.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(account_payload)))
request_create_account.setRestUrl("https://v3.recurly.com/accounts")
request_create_account.setRestRequestMethod("POST")
addAuthHeader(request_create_account)
addContentTypeHeader(request_create_account)
response_create_account = WSBuiltInKeywords.sendRequest(request_create_account)
WSBuiltInKeywords.verifyResponseStatusCode(response_create_account, 201)
account_id = new JsonSlurper().parseText(response_create_account.getResponseText())["id"]

// Step 3: Retrieve the account balance
request_get_balance = new RequestObject()
request_get_balance.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/balance")
request_get_balance.setRestRequestMethod("GET")
addAuthHeader(request_get_balance)
response_get_balance = WSBuiltInKeywords.sendRequest(request_get_balance)
WSBuiltInKeywords.verifyResponseStatusCode(response_get_balance, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}