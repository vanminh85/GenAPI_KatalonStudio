import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

import static com.kms.katalon.core.testobject.ConditionType.EQUALS

// Step 1: Create a new AccountAcquisitionUpdate resource with valid data
def acquisition_update_data = [
	"cost": [
		"currency": "USD",
		"amount": 10.0
	],
	"channel": "advertising",
	"subchannel": "online",
	"campaign": "test_campaign__unique__"
]

def request1 = new RequestObject()
request1.setRestUrl("https://v3.recurly.com/accounts/{account_id}/acquisition")
request1.setRestRequestMethod("PUT")
addAuthHeader(request1)
addContentTypeHeader(request1)
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(acquisition_update_data))))

def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Call the API POST /accounts/{account_id}/acquisition with the account_id parameter and the AccountAcquisitionUpdate data
def account_id = "test_account_id__unique__"
def request2 = new RequestObject()
request2.setRestUrl("https://v3.recurly.com/accounts/${account_id}/acquisition")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(acquisition_update_data))))

def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 422)

// Step 3: Verify that the response status code is 422
WSBuiltInKeywords.verifyResponseStatusCode(response2, 422)

def addAuthHeader(request) {
	def authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
	if (authToken) {
		def auth_header = new TestObjectProperty("authorization", EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	def content_type_header = new TestObjectProperty("content-type", EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

