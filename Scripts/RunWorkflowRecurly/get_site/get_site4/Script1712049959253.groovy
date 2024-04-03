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
addAuthHeader(address_request)
addContentTypeHeader(address_request)

def address_response = WSBuiltInKeywords.sendRequest(address_request)
WSBuiltInKeywords.verifyResponseStatusCode(address_response, 201)
def address_id = (new JsonSlurper()).parseText(address_response.getResponseText())["id"]

// Step 2: Create a new Site resource
def site_payload = [
	"id": "site123",
	"object": "site",
	"subdomain": "test",
	"public_api_key": "public_key",
	"mode": "development",
	"address": [
		"id": address_id
	]
]

def site_request = new RequestObject()
site_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(site_payload))))
site_request.setRestUrl("${GlobalVariable.base_url}/sites")
site_request.setRestRequestMethod("POST")
addAuthHeader(site_request)
addContentTypeHeader(site_request)

def site_response = WSBuiltInKeywords.sendRequest(site_request)
WSBuiltInKeywords.verifyResponseStatusCode(site_response, 201)
def site_id = (new JsonSlurper()).parseText(site_response.getResponseText())["id"]

// Step 3: Send a POST request to /sites/{site_id}
def post_payload = [
	"id": site_id
]

def post_request = new RequestObject()
post_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(post_payload))))
post_request.setRestUrl("${GlobalVariable.base_url}/sites/${site_id}")
post_request.setRestRequestMethod("POST")
addAuthHeader(post_request)
addContentTypeHeader(post_request)

def post_response = WSBuiltInKeywords.sendRequest(post_request)
WSBuiltInKeywords.verifyResponseStatusCode(post_response, 400)

// Step 4: Verify that the response status code is 400
assertThat(post_response.getStatusCode()).isEqualTo(400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
