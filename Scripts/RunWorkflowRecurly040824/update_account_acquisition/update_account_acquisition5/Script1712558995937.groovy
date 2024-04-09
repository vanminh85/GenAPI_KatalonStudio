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
create_account_request = new RequestObject()
create_account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([)
	"code": "test_account__unique__",
	"acquisition": [
		"cost": [
			"currency": "USD",
			"amount": 10.0
		],
		"channel": "direct_traffic",
		"subchannel": "online",
		"campaign": "summer_sale"
	],
	"external_accounts": []
])))
create_account_request.setRestUrl("https://v3.recurly.com/accounts")
create_account_request.setRestRequestMethod("POST")
addAuthHeader(create_account_request)
addContentTypeHeader(create_account_request)

create_account_response = WSBuiltInKeywords.sendRequest(create_account_request)
WSBuiltInKeywords.verifyResponseStatusCode(create_account_response, 201)

// Step 3: Extract the newly created account_id
new_account_id = new JsonSlurper().parseText(create_account_response.getResponseText())["id"]

// Step 4: Attempt to retrieve the account acquisition data for a non-existent account
get_non_existent_account_acquisition_request = new RequestObject()
get_non_existent_account_acquisition_request.setRestUrl("https://v3.recurly.com/accounts/non_existent_account_id/acquisition")
get_non_existent_account_acquisition_request.setRestRequestMethod("GET")
addAuthHeader(get_non_existent_account_acquisition_request)
addContentTypeHeader(get_non_existent_account_acquisition_request)

get_non_existent_account_acquisition_response = WSBuiltInKeywords.sendRequest(get_non_existent_account_acquisition_request)
WSBuiltInKeywords.verifyResponseStatusCode(get_non_existent_account_acquisition_response, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
