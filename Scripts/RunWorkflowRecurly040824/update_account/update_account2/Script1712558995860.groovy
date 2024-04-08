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
createAccountRequest = new RequestObject()
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
createAccountPayload = '{"code": "test_account__unique__", "email": "test@example.com", "first_name": "John", "last_name": "Doe"}'
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))
createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
createAccountId = new JsonSlurper().parseText(createAccountResponse.getResponseText()).get("id")

// Step 2: Prepare invalid data for the account update request
invalidUpdateData = '{"username": "invalid_username"}'

// Step 3: Update the account with invalid data
updateAccountRequest = new RequestObject()
updateAccountRequest.setRestUrl("https://v3.recurly.com/accounts/" + createAccountId)
updateAccountRequest.setRestRequestMethod("PUT")
addAuthHeader(updateAccountRequest)
addContentTypeHeader(updateAccountRequest)
updateAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(invalidUpdateData)))
updateAccountResponse = WSBuiltInKeywords.sendRequest(updateAccountRequest)

// Step 4: Verify the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(updateAccountResponse, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
