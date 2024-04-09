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
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new account
accountRequest = new RequestObject()
accountRequest.setRestUrl("https://v3.recurly.com/accounts")
accountRequest.setRestRequestMethod("POST")
addAuthHeader(accountRequest)
addContentTypeHeader(accountRequest)
accountPayload = '{"code": "test_account__unique__"}'
accountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountPayload)))
accountResponse = WSBuiltInKeywords.sendRequest(accountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(accountResponse, 201)
accountId = new JsonSlurper().parseText(accountResponse.getResponseText())["id"]

// Step 3: Create a new account acquisition
acquisitionRequest = new RequestObject()
acquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/" + accountId + "/acquisition")
acquisitionRequest.setRestRequestMethod("POST")
addAuthHeader(acquisitionRequest)
addContentTypeHeader(acquisitionRequest)
acquisitionPayload = '{"cost": {"currency": "USD", "amount": 100.0}, "channel": "email", "subchannel": "marketing", "campaign": "summer_sale"}'
acquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(acquisitionPayload)))
acquisitionResponse = WSBuiltInKeywords.sendRequest(acquisitionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(acquisitionResponse, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}