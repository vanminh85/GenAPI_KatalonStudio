import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.util.UUID

import static org.assertj.core.api.Assertions.*

// Step 1: Create a new account
def createAccountUrl = "https://v3.recurly.com/accounts"
def createAccountPayload = [
	"code": "test_account",
	"email": "test@example.com",
	"first_name": "John",
	"last_name": "Doe"
]
def createAccountRequest = new RequestObject()
createAccountRequest.setRestUrl(createAccountUrl)
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(createAccountPayload))))

def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)

// Step 2: Get the account ID from the response
def accountId = new JsonSlurper().parseText(createAccountResponse.getResponseText())["code"]

// Step 3: Call the API POST /accounts/{account_id}/balance with the account ID and an invalid request body
def postBalanceUrl = "https://v3.recurly.com/accounts/${accountId}/balance"
def invalidRequestBody = [
	"invalid_field": "invalid_value"
]
def postBalanceRequest = new RequestObject()
postBalanceRequest.setRestUrl(postBalanceUrl)
postBalanceRequest.setRestRequestMethod("POST")
addAuthHeader(postBalanceRequest)
addContentTypeHeader(postBalanceRequest)
postBalanceRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(invalidRequestBody))))

def postBalanceResponse = WSBuiltInKeywords.sendRequest(postBalanceRequest)
WSBuiltInKeywords.verifyResponseStatusCode(postBalanceResponse, 400)

// Step 4: Verify that the response status code is 400
assertThat(postBalanceResponse.getStatusCode()).isEqualTo(400)

def addAuthHeader(request) {
	def authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
	if (authToken) {
		def authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	def contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
