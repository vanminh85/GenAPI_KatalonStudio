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

// Request 1: Create Account
def accountData = '{"code": "test_code__unique__", "first_name": "John", "last_name": "Doe", "email": "john.doe@example.com"}'
def createAccountRequest = new RequestObject()
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountData)))
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)

def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
