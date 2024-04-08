import internal.GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords

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

// Step 1
def account_acquisition_update = [
	"cost": [
		"currency": "USD",
		"amount": 100
	],
	"channel": "advertising",
	"subchannel": "online",
	"campaign": "summer_sale"
]

// Step 2
def account_id = "valid_account_id"
def url = "${GlobalVariable.base_url}/accounts/${account_id}/acquisition"
def request = new RequestObject()
request.setRestUrl(url)
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(account_acquisition_update))))

def response = WSBuiltInKeywords.sendRequest(request)

// Step 3
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

println("Test case passed: test_post_validAccountAcquisition_returns200")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

