import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.util.UUID

import static org.junit.Assert.assertEquals

import com.kms.katalon.core.testobject.ResponseObject

// Step 1: Create a new Category
def category_payload = [
	"name": "Test Category__unique__"
]
def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))
category_request.setRestUrl("https://petstore.swagger.io/v2/pet")
category_request.setRestRequestMethod("POST")
addContentTypeHeader(category_request)
addAuthHeader(category_request)
def category_response = WSBuiltInKeywords.sendRequest(category_request)
def category_id = (new JsonSlurper().parseText(category_response.getResponseText()))["id"]
assertEquals(200, category_response.getStatusCode())

// Step 2: Create a new Pet
def pet_payload = [
	"name": "Test Pet__unique__",
	"photoUrls": ["http://test.com/image.jpg"],
	"category": [
		"id": category_id,
		"name": "Test Category__unique__"
	]
]
def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addContentTypeHeader(pet_request)
addAuthHeader(pet_request)
def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
def pet_id = (new JsonSlurper().parseText(pet_response.getResponseText()))["id"]
assertEquals(200, pet_response.getStatusCode())

// Step 3: Place an order
def order_payload = [
	"petId": pet_id,
	"quantity": 1,
	"status": "placed",
	"complete": true
]
def order_request = new RequestObject()
order_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(order_payload))))
order_request.setRestUrl("https://petstore.swagger.io/v2/store/order")
order_request.setRestRequestMethod("POST")
addContentTypeHeader(order_request)
addAuthHeader(order_request)
def order_response = WSBuiltInKeywords.sendRequest(order_request)
assertEquals(400, order_response.getStatusCode())

// Step 4: Verify the response status code
assertEquals(400, order_response.getStatusCode())

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
