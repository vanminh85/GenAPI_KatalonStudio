import internal.GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords

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

// Step 1: Create a new Category
def category_payload = [
	"id": 1,
	"name": "Test Category"
]

def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))
category_request.setRestUrl("https://petstore.swagger.io/v2/pet")
category_request.setRestRequestMethod("POST")
addAuthHeader(category_request)
addContentTypeHeader(category_request)

def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

// Step 2: Create a new Pet
def pet_payload = [
	"id": 1,
	"category": [
		"id": 1,
		"name": "Test Category"
	],
	"name": "Test Pet",
	"photoUrls": ["http://test.com/image.jpg"]
]

def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)

def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

// Step 3: Create a new Order
def order_payload = [
	"id": 1,
	"petId": 1,
	"quantity": 1,
	"shipDate": "2022-01-01T00:00:00Z",
	"status": "placed",
	"complete": true
]

def order_request = new RequestObject()
order_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(order_payload))))
order_request.setRestUrl("https://petstore.swagger.io/v2/store/order")
order_request.setRestRequestMethod("POST")
addAuthHeader(order_request)
addContentTypeHeader(order_request)

def order_response = WSBuiltInKeywords.sendRequest(order_request)
WSBuiltInKeywords.verifyResponseStatusCode(order_response, 200)

// Step 4: Send a POST request to /store/order/1
def order_request_2 = new RequestObject()
order_request_2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(order_payload))))
order_request_2.setRestUrl("https://petstore.swagger.io/v2/store/order/1")
order_request_2.setRestRequestMethod("POST")
addAuthHeader(order_request_2)
addContentTypeHeader(order_request_2)

def order_response_2 = WSBuiltInKeywords.sendRequest(order_request_2)
WSBuiltInKeywords.verifyResponseStatusCode(order_response_2, 200)

// Step 5: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(order_response_2, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
