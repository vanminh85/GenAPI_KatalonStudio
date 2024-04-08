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

def acquisitionUpdateRequest = new RequestObject()
acquisitionUpdateRequest.setRestUrl(GlobalVariable.base_url + "/accounts/{account_id}/acquisition")
acquisitionUpdateRequest.setRestRequestMethod("PUT")
acquisitionUpdateRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(acquisition_update_payload))))

addAuthHeader(acquisitionUpdateRequest)
addContentTypeHeader(acquisitionUpdateRequest)

def acquisitionUpdateResponse = WSBuiltInKeywords.sendRequest(acquisitionUpdateRequest)
WSBuiltInKeywords.verifyResponseStatusCode(acquisitionUpdateResponse, 200)

// Step 2: Create a new campaign resource with valid data
def campaign_payload = [
	"name": "test_campaign__unique__"
]

def createCampaignRequest = new RequestObject()
createCampaignRequest.setRestUrl(GlobalVariable.base_url + "/campaigns")
createCampaignRequest.setRestRequestMethod("POST")
createCampaignRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(campaign_payload))))

addAuthHeader(createCampaignRequest)
addContentTypeHeader(createCampaignRequest)

def createCampaignResponse = WSBuiltInKeywords.sendRequest(createCampaignRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createCampaignResponse, 200)

// Step 3: Call the API POST /accounts/{account_id}/acquisition with an invalid account_id parameter
def invalid_account_id = "invalid_account_id__unique__"

def invalidAccountIdRequest = new RequestObject()
invalidAccountIdRequest.setRestUrl(GlobalVariable.base_url + "/accounts/{invalid_account_id}/acquisition")
invalidAccountIdRequest.setRestRequestMethod("POST")
invalidAccountIdRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(acquisition_update_payload))))

addAuthHeader(invalidAccountIdRequest)
addContentTypeHeader(invalidAccountIdRequest)

def invalidAccountIdResponse = WSBuiltInKeywords.sendRequest(invalidAccountIdRequest)
WSBuiltInKeywords.verifyResponseStatusCode(invalidAccountIdResponse, 404)

// Step 4: Verify that the response status code is 404
WSBuiltInKeywords.verifyResponseStatusCode(invalidAccountIdResponse, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
