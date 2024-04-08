import internal.GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
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

def base_url = "https://v3.recurly.com"

// Step 1: Create a new AccountAcquisitionUpdate resource
def acquisition_update_data = [
	"cost": [
		"currency": "USD",
		"amount": 100
	],
	"channel": "advertising",
	"subchannel": "online",
	"campaign": "summer_sale"
]

def acquisition_update_request = new RequestObject()
acquisition_update_request.setRestUrl(base_url + "/accounts/{account_id}/acquisition_update")
acquisition_update_request.setRestRequestMethod("POST")
acquisition_update_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(acquisition_update_data))))

addAuthHeader(acquisition_update_request)
addContentTypeHeader(acquisition_update_request)

def acquisition_update_response = WSBuiltInKeywords.sendRequest(acquisition_update_request)
WSBuiltInKeywords.verifyResponseStatusCode(acquisition_update_response, 200)

def acquisition_update_id = new JsonSlurper().parseText(acquisition_update_response.getResponseText())["id"]

// Step 2: Create a new AccountAcquisition resource
def account_acquisition_data = [
	"id": "<generated_account_id>",
	"object": "account_acquisition",
	"account": "<existing_account_data>",
	"created_at": new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
	"updated_at": new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
	"campaign": acquisition_update_id
]

def account_acquisition_request = new RequestObject()
account_acquisition_request.setRestUrl(base_url + "/accounts/{account_id}/acquisition")
account_acquisition_request.setRestRequestMethod("POST")
account_acquisition_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(account_acquisition_data))))

addAuthHeader(account_acquisition_request)
addContentTypeHeader(account_acquisition_request)

def account_acquisition_response = WSBuiltInKeywords.sendRequest(account_acquisition_request)
WSBuiltInKeywords.verifyResponseStatusCode(account_acquisition_response, 200)

// Step 3: Call the API POST /accounts/{account_id}/acquisition
def account_id = "<generated_account_id>"
def post_acquisition_url = account_acquisition_request.getRestUrl().replace("{account_id}", account_id)

def post_acquisition_request = new RequestObject()
post_acquisition_request.setRestUrl(post_acquisition_url)
post_acquisition_request.setRestRequestMethod("POST")

addAuthHeader(post_acquisition_request)

def post_acquisition_response = WSBuiltInKeywords.sendRequest(post_acquisition_request)
WSBuiltInKeywords.verifyResponseStatusCode(post_acquisition_response, 200)

// Step 4: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(post_acquisition_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
