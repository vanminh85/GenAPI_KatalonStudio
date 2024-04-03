import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

import static org.assertj.core.api.Assertions.*

// Import GlobalVariable
import internal.GlobalVariable

// Define addAuthHeader function
def addAuthHeader(request) {
	def authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		def auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

// Define addContentTypeHeader function
def addContentTypeHeader(request) {
	def content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

// Define uuid variable
uuid = UUID.randomUUID().toString()

// Step 1: Create a new AccountAcquisitionUpdate resource with valid data
def acquisition_update_payload = [
	"cost": [
		"currency": "USD",
		"amount": 10.0
	],
	"channel": "advertising",
	"subchannel": "online",
	"campaign": "test_campaign__unique__"
]

def acquisition_update_request = new RequestObject()
acquisition_update_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(acquisition_update_payload))))
acquisition_update_request.setRestUrl(GlobalVariable.base_url + "/accounts/{account_id}/acquisition")
acquisition_update_request.setRestRequestMethod("PUT")
addAuthHeader(acquisition_update_request)
addContentTypeHeader(acquisition_update_request)

def acquisition_update_response = WSBuiltInKeywords.sendRequest(acquisition_update_request)
WSBuiltInKeywords.verifyResponseStatusCode(acquisition_update_response, 200)

// Step 2: Create a new campaign resource with valid data
def campaign_payload = [
	"name": "test_campaign__unique__"
]

def campaign_request = new RequestObject()
campaign_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(campaign_payload))))
campaign_request.setRestUrl(GlobalVariable.base_url + "/campaigns")
campaign_request.setRestRequestMethod("POST")
addAuthHeader(campaign_request)
addContentTypeHeader(campaign_request)

def campaign_response = WSBuiltInKeywords.sendRequest(campaign_request)
WSBuiltInKeywords.verifyResponseStatusCode(campaign_response, 200)

// Step 3: Call the API POST /accounts/{account_id}/acquisition without providing the account_id parameter
def invalid_acquisition_request = new RequestObject()
invalid_acquisition_request.setRestUrl(GlobalVariable.base_url + "/accounts/acquisition")
invalid_acquisition_request.setRestRequestMethod("POST")
addAuthHeader(invalid_acquisition_request)
addContentTypeHeader(invalid_acquisition_request)

def invalid_acquisition_response = WSBuiltInKeywords.sendRequest(invalid_acquisition_request)
WSBuiltInKeywords.verifyResponseStatusCode(invalid_acquisition_response, 404)

// Step 4: Verify that the response status code is 404
assertThat(invalid_acquisition_response.getStatusCode()).isEqualTo(404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
