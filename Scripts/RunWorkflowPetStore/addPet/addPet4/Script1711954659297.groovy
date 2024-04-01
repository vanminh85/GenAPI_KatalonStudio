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

// Step 1: Create a new category with name 'Test Category'
def category_payload = [
	"name": "Test Category__unique__"
]

def category_request = new RequestObject()
category_request.setRestUrl("${GlobalVariable.base_url}/category")
category_request.setRestRequestMethod("POST")
addContentTypeHeader(category_request)
addAuthHeader(category_request)
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))

def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

def category_id = (new JsonSlurper().parseText(category_response.getResponseText()))["id"]

// Step 2: Create a new pet with name 'Test Pet', photoUrls ['http://example.com/image.jpg'], and category ID from step 1
def pet_payload = [
	"name": "Test Pet__unique__",
	"photoUrls": ["http://example.com/image.jpg"],
	"category": [
		"id": category_id,
		"name": "Test Category__unique__"
	]
]

def pet_request = new RequestObject()
pet_request.setRestUrl("${GlobalVariable.base_url}/pet")
pet_request.setRestRequestMethod("POST")
addContentTypeHeader(pet_request)
addAuthHeader(pet_request)
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))

def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

def pet_id = (new JsonSlurper().parseText(pet_response.getResponseText()))["id"]

// Step 3: Send a POST request to /pet with the created pet data
def duplicate_pet_request = new RequestObject()
duplicate_pet_request.setRestUrl("${GlobalVariable.base_url}/pet")
duplicate_pet_request.setRestRequestMethod("POST")
addContentTypeHeader(duplicate_pet_request)
addAuthHeader(duplicate_pet_request)
duplicate_pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))

def duplicate_pet_response = WSBuiltInKeywords.sendRequest(duplicate_pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(duplicate_pet_response, 200)

// Step 4: Verify that the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(duplicate_pet_response, 200)

// Step 5: Send another POST request to /pet with the same pet data
def invalid_duplicate_pet_request = new RequestObject()
invalid_duplicate_pet_request.setRestUrl("${GlobalVariable.base_url}/pet")
invalid_duplicate_pet_request.setRestRequestMethod("POST")
addContentTypeHeader(invalid_duplicate_pet_request)
addAuthHeader(invalid_duplicate_pet_request)
invalid_duplicate_pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))

def invalid_duplicate_pet_response = WSBuiltInKeywords.sendRequest(invalid_duplicate_pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(invalid_duplicate_pet_response, 405)

// Step 6: Verify that the response status code is 405
WSBuiltInKeywords.verifyResponseStatusCode(invalid_duplicate_pet_response, 405)

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