import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

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

// Base URL
base_url = "https://petstore.swagger.io/v2"

// Test Case
test_case = [
	name: "test_post_invalidPetId_returns404",
	steps: [
		"1. Call the API POST /pet/999",
		"2. Verify that the response status code is 404"
	]
]

// Step 1: Call the API POST /pet/999
endpoint = "/pet/999"
url = base_url + endpoint
request = new RequestObject()
request.setRestUrl(url)
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)
response = WSBuiltInKeywords.sendRequest(request)

// Step 2: Verify that the response status code is 404
expected_status_code = 404
actual_status_code = response.getStatusCode()
WSBuiltInKeywords.verifyResponseStatusCode(response, expected_status_code)

println("Test case executed successfully.")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
