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

def base_url = "https://petstore.swagger.io/v2"

// Step 1: Create a new Category resource
def category_payload = [
	"id": 1,
	"name": "category1"
]
def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))
category_request.setRestUrl("${base_url}/pet")
category_request.setRestRequestMethod("POST")
addAuthHeader(category_request)
addContentTypeHeader(category_request)
def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

// Step 2: Create a new Pet resource
def pet_payload = [
	"id": 1,
	"name": "pet1",
	"photoUrls": ["url1"],
	"category": [
		"id": 1,
		"name": "category1"
	]
]
def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))
pet_request.setRestUrl("${base_url}/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)
def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

// Step 3: Create a new Order resource
def order_payload = [
	"id": 1,
	"petId": 1,
	"quantity": 1,
	"status": "placed"
]
def order_request = new RequestObject()
order_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(order_payload))))
order_request.setRestUrl("${base_url}/store/order")
order_request.setRestRequestMethod("POST")
addAuthHeader(order_request)
addContentTypeHeader(order_request)
def order_response = WSBuiltInKeywords.sendRequest(order_request)
WSBuiltInKeywords.verifyResponseStatusCode(order_response, 200)

// Step 4: Send a POST request to /user/{username}
def user_payload = [
	"id": 1,
	"username": "user1"
]
def user_request = new RequestObject()
user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(user_payload))))
user_request.setRestUrl("${base_url}/user/user1")
user_request.setRestRequestMethod("POST")
addAuthHeader(user_request)
addContentTypeHeader(user_request)
def user_response = WSBuiltInKeywords.sendRequest(user_request)
WSBuiltInKeywords.verifyResponseStatusCode(user_response, 200)

// Step 5: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(user_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
