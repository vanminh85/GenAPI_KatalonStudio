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
createAccountRequest = new RequestObject()
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([)
	"code": "test_account__unique__",
	"email": "test@example.com",
	"first_name": "John",
	"last_name": "Doe"
])))
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
createAccountData = new JsonSlurper().parseText(createAccountResponse.getResponseText())
accountId = createAccountData.get("id")

// Step 3: Send a GET request to /accounts/{account_id}
getAccountRequest = new RequestObject()
getAccountRequest.setRestUrl("https://v3.recurly.com/accounts/" + accountId)
getAccountRequest.setRestRequestMethod("GET")
addAuthHeader(getAccountRequest)
getAccountResponse = WSBuiltInKeywords.sendRequest(getAccountRequest)

// Step 4: Verify that the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(getAccountResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
