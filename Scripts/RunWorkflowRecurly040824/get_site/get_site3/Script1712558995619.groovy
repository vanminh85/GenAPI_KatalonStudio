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

// Step 1: Create a new Settings resource
def settings_payload = [
	"billing_address_requirement": "full",
	"accepted_currencies": ["USD"],
	"default_currency": "USD"
]

def settings_request = new RequestObject()
settings_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(settings_payload))))
settings_request.setRestUrl(base_url + "/settings")
settings_request.setRestRequestMethod("POST")
addAuthHeader(settings_request)
addContentTypeHeader(settings_request)

def settings_response = WSBuiltInKeywords.sendRequest(settings_request)
WSBuiltInKeywords.verifyResponseStatusCode(settings_response, 201)
def settings_id = new JsonSlurper().parseText(settings_response.getResponseText())["id"]

// Step 2: Create a new Site resource
def site_payload = [
	"id": "site_id__unique__",
	"object": "site_object__unique__",
	"subdomain": "site_subdomain__unique__",
	"public_api_key": "site_public_api_key__unique__",
	"mode": "development",
	"address": [
		"phone": "site_phone__unique__",
		"street1": "site_street1__unique__",
		"street2": "site_street2__unique__",
		"city": "site_city__unique__",
		"region": "site_region__unique__",
		"postal_code": "site_postal_code__unique__",
		"country": "site_country__unique__",
		"geo_code": "site_geo_code__unique__"
	],
	"settings": settings_id,
	"features": ["credit_memos", "manual_invoicing"],
	"created_at": "2022-01-01T00:00:00Z",
	"updated_at": "2022-01-01T00:00:00Z",
	"deleted_at": "2022-01-01T00:00:00Z"
]

def site_request = new RequestObject()
site_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(site_payload))))
site_request.setRestUrl(base_url + "/sites")
site_request.setRestRequestMethod("POST")
addAuthHeader(site_request)
addContentTypeHeader(site_request)

def site_response = WSBuiltInKeywords.sendRequest(site_request)
WSBuiltInKeywords.verifyResponseStatusCode(site_response, 201)
def site_id = new JsonSlurper().parseText(site_response.getResponseText())["id"]

// Step 3: Send a POST request to /sites/{site_id}
def post_payload = [
	"id": site_id,
	"object": "site_object__unique__",
	"subdomain": "site_subdomain__unique__",
	"public_api_key": "site_public_api_key__unique__",
	"mode": "development",
	"address": [
		"phone": "site_phone__unique__",
		"street1": "site_street1__unique__",
		"street2": "site_street2__unique__",
		"city": "site_city__unique__",
		"region": "site_region__unique__",
		"postal_code": "site_postal_code__unique__",
		"country": "site_country__unique__",
		"geo_code": "site_geo_code__unique__"
	],
	"settings": settings_id,
	"features": ["credit_memos", "manual_invoicing"],
	"created_at": "2022-01-01T00:00:00Z",
	"updated_at": "2022-01-01T00:00:00Z",
	"deleted_at": "2022-01-01T00:00:00Z"
]

def post_request = new RequestObject()
post_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(post_payload))))
post_request.setRestUrl(base_url + "/sites/" + site_id)
post_request.setRestRequestMethod("POST")
addAuthHeader(post_request)
addContentTypeHeader(post_request)

def post_response = WSBuiltInKeywords.sendRequest(post_request)
WSBuiltInKeywords.verifyResponseStatusCode(post_response, 400)

// Step 4: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(post_response, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
