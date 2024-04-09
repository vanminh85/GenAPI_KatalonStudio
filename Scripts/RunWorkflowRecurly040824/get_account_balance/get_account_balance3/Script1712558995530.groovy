import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
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

// Step 1: Create a new account with missing required fields
accountPayload = '{"code": "test_account__unique__"}'
accountRequest = new RequestObject()
accountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountPayload)))
accountRequest.setRestUrl("https://v3.recurly.com/accounts")
accountRequest.setRestRequestMethod("POST")
addAuthHeader(accountRequest)
addContentTypeHeader(accountRequest)
accountResponse = WSBuiltInKeywords.sendRequest(accountRequest)
accountId = new JsonSlurper().parseText(accountResponse.getResponseText()).get("id")

// Step 2: Get the account balance for the newly created account
balanceRequest = new RequestObject()
balanceRequest.setRestUrl("https://v3.recurly.com/accounts/" + accountId + "/balance")
balanceRequest.setRestRequestMethod("POST")
addAuthHeader(balanceRequest)
balanceResponse = WSBuiltInKeywords.sendRequest(balanceRequest)

// Step 3: Verify the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(balanceResponse, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}