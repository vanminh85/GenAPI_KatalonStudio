import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

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
	"name": "Test Category"
]
def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))
category_request.setRestUrl("${base_url}/category")
category_request.setRestRequestMethod("POST")
addContentTypeHeader(category_request)
def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

// Step 2: Create a new Pet resource
def pet_payload = [
	"id": 1,
	"category": [
		"id": 1
	],
	"name": "Test Pet",
	"photoUrls": [
		"http://example.com/photo1.jpg"
	]
]
def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))
pet_request.setRestUrl("${base_url}/pet")
pet_request.setRestRequestMethod("POST")
addContentTypeHeader(pet_request)
def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

// Step 3: Create a new Order resource
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
order_request.setRestUrl("${base_url}/store/order")
order_request.setRestRequestMethod("POST")
addContentTypeHeader(order_request)
def order_response = WSBuiltInKeywords.sendRequest(order_request)
WSBuiltInKeywords.verifyResponseStatusCode(order_response, 200)

// Step 4: Create a new User resource
def user_payload = [
	"id": 1,
	"username": "testuser",
	"firstName": "Test",
	"lastName": "User",
	"email": "testuser@example.com",
	"password": "password",
	"phone": "1234567890",
	"userStatus": 1
]
def user_request = new RequestObject()
user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(user_payload))))
user_request.setRestUrl("${base_url}/user")
user_request.setRestRequestMethod("POST")
addContentTypeHeader(user_request)
def user_response = WSBuiltInKeywords.sendRequest(user_request)
WSBuiltInKeywords.verifyResponseStatusCode(user_response, 200)

// Step 5: Send a POST request to /user with the duplicate username
def duplicate_user_payload = [
	"id": 2,
	"username": "testuser",
	"firstName": "Test2",
	"lastName": "User2",
	"email": "testuser2@example.com",
	"password": "password2",
	"phone": "9876543210",
	"userStatus": 2
]
def duplicate_user_request = new RequestObject()
duplicate_user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(duplicate_user_payload))))
duplicate_user_request.setRestUrl("${base_url}/user")
duplicate_user_request.setRestRequestMethod("POST")
addContentTypeHeader(duplicate_user_request)
def duplicate_user_response = WSBuiltInKeywords.sendRequest(duplicate_user_request)
WSBuiltInKeywords.verifyResponseStatusCode(duplicate_user_response, 400)

// Step 6: Verify the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(duplicate_user_response, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

