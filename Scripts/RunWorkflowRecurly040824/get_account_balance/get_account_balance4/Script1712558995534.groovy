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

def accountRequest = new RequestObject()
accountRequest.setRestUrl("https://v3.recurly.com/accounts")
accountRequest.setRestRequestMethod("POST")
addAuthHeader(accountRequest)
addContentTypeHeader(accountRequest)
def accountPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	"code": "test_account__unique__",
	"first_name": "John",
	"last_name": "Doe",
	"email": "johndoe@example.com"
])))
accountRequest.setBodyContent(accountPayload)
def accountResponse = WSBuiltInKeywords.sendRequest(accountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(accountResponse, 201)
def account_id = new JsonSlurper().parseText(accountResponse.getResponseText())["id"]

def billingRequest = new RequestObject()
billingRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/billing_infos")
billingRequest.setRestRequestMethod("POST")
addAuthHeader(billingRequest)
addContentTypeHeader(billingRequest)
def billingPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	"token_id": "billing_token__unique__",
	"first_name": "John",
	"last_name": "Doe",
	"number": "4111111111111111",
	"month": "12",
	"year": "2023",
	"cvv": "123",
	"currency": "USD"
])))
billingRequest.setBodyContent(billingPayload)
def billingResponse = WSBuiltInKeywords.sendRequest(billingRequest)
WSBuiltInKeywords.verifyResponseStatusCode(billingResponse, 201)

def balanceRequest = new RequestObject()
balanceRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/balance")
balanceRequest.setRestRequestMethod("POST")
addAuthHeader(balanceRequest)
def balanceResponse = WSBuiltInKeywords.sendRequest(balanceRequest)
WSBuiltInKeywords.verifyResponseStatusCode(balanceResponse, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}