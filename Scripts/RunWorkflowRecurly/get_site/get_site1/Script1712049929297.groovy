import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

import static org.assertj.core.api.Assertions.*

// Step 1: Create a new Address resource
def address_payload = [
	"phone": "1234567890",
	"street1": "123 Main St",
	"city": "New York",
	"region": "NY",
	"postal_code": "10001",
	"country": "US"
]

def address_request = new RequestObject()
address_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(address_payload))))
address_request.setRestUrl("${GlobalVariable.base_url}/addresses")
address_request.setRestRequestMethod("POST")
addContentTypeHeader(address_request)
addAuthHeader(address_request)

def address_response = WSBuiltInKeywords.sendRequest(address_request)
WSBuiltInKeywords.verifyResponseStatusCode(address_response, 201)
def address_id = (new JsonSlurper().parseText(address_response.getResponseText()))["id"]

// Step 2: Create a new Settings resource
def settings_payload = [
	"billing_address_requirement": "full",
	"accepted_currencies": ["USD"],
	"default_currency": "USD"
]

def settings_request = new RequestObject()
settings_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(settings_payload))))
settings_request.setRestUrl("${GlobalVariable.base_url}/settings")
settings_request.setRestRequestMethod("POST")
addContentTypeHeader(settings_request)
addAuthHeader(settings_request)

def settings_response = WSBuiltInKeywords.sendRequest(settings_request)
WSBuiltInKeywords.verifyResponseStatusCode(settings_response, 201)
def settings_id = (new JsonSlurper().parseText(settings_response.getResponseText()))["id"]

// Step 3: Create a new Site resource
def site_payload = [
	"id": "site123",
	"object": "site",
	"subdomain": "example",
	"public_api_key": "public_key",
	"mode": "development",
	"address": address_id,
	"settings": settings_id
]

def site_request = new RequestObject()
site_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(site_payload))))
site_request.setRestUrl("${GlobalVariable.base_url}/sites/site123")
site_request.setRestRequestMethod("POST")
addContentTypeHeader(site_request)
addAuthHeader(site_request)

def site_response = WSBuiltInKeywords.sendRequest(site_request)
WSBuiltInKeywords.verifyResponseStatusCode(site_response, 200)

// Step 5: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(site_response, 200)

println("Test case passed: test_post_validSiteId_returns200")

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

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
