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

// Step 1: Create a Category resource
def category_payload = [
	"id": 1,
	"name": "category__unique__"
]
def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))
category_request.setRestUrl("${GlobalVariable.base_url}/category")
category_request.setRestRequestMethod("POST")
addContentTypeHeader(category_request)
addAuthHeader(category_request)
def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)
def category_id = (new JsonSlurper().parseText(category_response.getResponseText()))["id"]

// Step 2: Create a Pet resource
def pet_payload = [
	"name": "pet__unique__",
	"photoUrls": ["photoUrl1", "photoUrl2"],
	"category": [
		"id": category_id,
		"name": "category__unique__"
	]
]
def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))
pet_request.setRestUrl("${GlobalVariable.base_url}/pet")
pet_request.setRestRequestMethod("POST")
addContentTypeHeader(pet_request)
addAuthHeader(pet_request)
def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)
def pet_id = (new JsonSlurper().parseText(pet_response.getResponseText()))["id"]

// Step 3: Create an Order resource
def order_payload = [
	"petId": pet_id,
	"quantity": 1
]
def order_request = new RequestObject()
order_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(order_payload))))
order_request.setRestUrl("${GlobalVariable.base_url}/store/order")
order_request.setRestRequestMethod("POST")
addContentTypeHeader(order_request)
addAuthHeader(order_request)
def order_response = WSBuiltInKeywords.sendRequest(order_request)
WSBuiltInKeywords.verifyResponseStatusCode(order_response, 200)
def order_id = (new JsonSlurper().parseText(order_response.getResponseText()))["id"]

// Step 4: Create a User resource with missing required field
def user_payload = [
	"username": "user__unique__"
]
def user_request = new RequestObject()
user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(user_payload))))
user_request.setRestUrl("${GlobalVariable.base_url}/user")
user_request.setRestRequestMethod("POST")
addContentTypeHeader(user_request)
addAuthHeader(user_request)
def user_response = WSBuiltInKeywords.sendRequest(user_request)
WSBuiltInKeywords.verifyResponseStatusCode(user_response, 400)

// Step 5: Create an array of User resources
def user_array_payload = [
	[
		"username": "user1__unique__",
		"userStatus": 1
	],
	[
		"username": "user2__unique__",
		"userStatus": 2
	]
]
def user_array_request = new RequestObject()
user_array_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(user_array_payload))))
user_array_request.setRestUrl("${GlobalVariable.base_url}/user/createWithArray")
user_array_request.setRestRequestMethod("POST")
addContentTypeHeader(user_array_request)
addAuthHeader(user_array_request)
def user_array_response = WSBuiltInKeywords.sendRequest(user_array_request)
WSBuiltInKeywords.verifyResponseStatusCode(user_array_response, 200)

// Step 7: Verify the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(user_array_response, 400)

// Function to add authorization header
def addAuthHeader(request) {
	def authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		def auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

// Function to add Content-Type header
def addContentTypeHeader(request) {
	def content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

// Variable without using def keyword
uuid = UUID.randomUUID().toString()

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

