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

// Step 1: Create a new category with id=1 and name='Test Category'
def category_payload = [
	"id": 1,
	"name": "Test Category"
]

def category_request = new RequestObject()
category_request.setRestUrl("https://petstore.swagger.io/v2/category")
category_request.setRestRequestMethod("POST")
addContentTypeHeader(category_request)
addAuthHeader(category_request)
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))

def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

// Step 2: Create a new pet with id=1, name='Test Pet', photoUrls=['http://test.com/image.jpg'], and category=1
def pet_payload = [
	"id": 1,
	"name": "Test Pet",
	"photoUrls": ["http://test.com/image.jpg"],
	"category": [
		"id": 1,
		"name": "Test Category"
	]
]

def pet_request = new RequestObject()
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addContentTypeHeader(pet_request)
addAuthHeader(pet_request)
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))

def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

// Step 3: Send a POST request to /pet with the created pet data
def pet_request_2 = new RequestObject()
pet_request_2.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request_2.setRestRequestMethod("POST")
addContentTypeHeader(pet_request_2)
addAuthHeader(pet_request_2)
pet_request_2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))

def pet_response_2 = WSBuiltInKeywords.sendRequest(pet_request_2)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response_2, 200)

// Step 4: Verify that the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(pet_response_2, 200)

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
