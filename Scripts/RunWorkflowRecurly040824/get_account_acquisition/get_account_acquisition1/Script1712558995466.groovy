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
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([)
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
])))
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)

def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
def account_id = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

// Step 3: Create acquisition data for the account
def createAcquisitionRequest = new RequestObject()
createAcquisitionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([)
	cost: [
		currency: "USD",
		amount: 50.0
	],
	channel: "referral",
	subchannel: "word_of_mouth",
	campaign: "friend_referral"
])))
createAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/acquisition")
createAcquisitionRequest.setRestRequestMethod("POST")
addAuthHeader(createAcquisitionRequest)
addContentTypeHeader(createAcquisitionRequest)

def createAcquisitionResponse = WSBuiltInKeywords.sendRequest(createAcquisitionRequest)

// Step 4: Verify the response status code is 201
WSBuiltInKeywords.verifyResponseStatusCode(createAcquisitionResponse, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
