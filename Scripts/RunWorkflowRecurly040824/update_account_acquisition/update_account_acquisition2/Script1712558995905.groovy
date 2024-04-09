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
		auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new account
createAccountRequest = new RequestObject()
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([)
	code: "test_account__unique__",
	acquisition: [
		cost: [
			currency: "USD",
			amount: 100.0
		],
		channel: "direct_traffic",
		subchannel: "organic"
	],
	external_accounts: []
])))
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)

createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createAccountResponse, 201)

// Step 3: Extract the newly created account_id
accountId = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

// Step 4: Retrieve the account acquisition data
getAccountAcquisitionRequest = new RequestObject()
getAccountAcquisitionRequest.setRestUrl("https://v3.recurly.com/accounts/${accountId}/acquisition")
getAccountAcquisitionRequest.setRestRequestMethod("GET")
addAuthHeader(getAccountAcquisitionRequest)

getAccountAcquisitionResponse = WSBuiltInKeywords.sendRequest(getAccountAcquisitionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(getAccountAcquisitionResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
