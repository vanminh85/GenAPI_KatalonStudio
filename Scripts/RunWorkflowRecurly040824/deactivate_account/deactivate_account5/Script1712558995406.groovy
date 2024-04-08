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

// Step 1: Create a new account
request1 = new RequestObject()
payload1 = '{"code": "test_account__unique__", "acquisition": {}, "external_accounts": [], "shipping_addresses": []}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))
request1.setRestUrl("https://v3.recurly.com/accounts")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
accountId = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 3: Deactivate the account
request2 = new RequestObject()
request2.setRestUrl("https://v3.recurly.com/accounts/" + accountId)
request2.setRestRequestMethod("DELETE")
addAuthHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)

// Step 4: Verify the response status code
assert WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
