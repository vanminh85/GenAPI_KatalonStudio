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
def createAccountRequest = new RequestObject()
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)

def createAccountPayload = JsonOutput.toJson([
	code: "test_account__unique__",
	acquisition: [
		cost: [
			currency: "USD",
			amount: 100.0
		],
		channel: "direct_traffic",
		subchannel: "online_ads",
		campaign: "summer_sale"
	],
	shipping_addresses: []
])

createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))

def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
def account_id = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

// Step 3: Create acquisition data for the account
def createAcquisitionRequest = new RequestObject()
createAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/acquisition")
createAcquisitionRequest.setRestRequestMethod("POST")
addAuthHeader(createAcquisitionRequest)
addContentTypeHeader(createAcquisitionRequest)

def createAcquisitionPayload = JsonOutput.toJson([
	cost: [
		currency: "USD",
		amount: 100.0
	],
	channel: "direct_traffic",
	subchannel: "online_ads",
	campaign: "summer_sale"
])

createAcquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAcquisitionPayload)))

def createAcquisitionResponse = WSBuiltInKeywords.sendRequest(createAcquisitionRequest)

// Step 4: Retrieve the acquisition data
def getAcquisitionRequest = new RequestObject()
getAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/acquisition")
getAcquisitionRequest.setRestRequestMethod("GET")
addAuthHeader(getAcquisitionRequest)

def getAcquisitionResponse = WSBuiltInKeywords.sendRequest(getAcquisitionRequest)

// Step 5: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(getAcquisitionResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
