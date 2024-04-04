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

import com.kms.katalon.core.testobject.ResponseObject

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

def category_payload = [
	"id": 1,
	"name": "TestCategory"
]

def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))
category_request.setRestUrl("https://petstore.swagger.io/v2/pet")
category_request.setRestRequestMethod("POST")
addAuthHeader(category_request)
addContentTypeHeader(category_request)

def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

def pet_payload = [
	"id": 1,
	"name": "TestPet",
	"category": [
		"id": 1
	]
]

def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)

def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

def order_payload = [
	"id": 1,
	"petId": 1,
	"status": "placed"
]

def order_request = new RequestObject()
order_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(order_payload))))
order_request.setRestUrl("https://petstore.swagger.io/v2/store/order")
order_request.setRestRequestMethod("POST")
addAuthHeader(order_request)
addContentTypeHeader(order_request)

def order_response = WSBuiltInKeywords.sendRequest(order_request)
WSBuiltInKeywords.verifyResponseStatusCode(order_response, 200)

def user_payload = [
	"id": 1,
	"username": "TestUser",
	"userStatus": 1
]

def user_request = new RequestObject()
user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))
user_request.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
user_request.setRestRequestMethod("POST")
addAuthHeader(user_request)
addContentTypeHeader(user_request)

def user_response = WSBuiltInKeywords.sendRequest(user_request)
WSBuiltInKeywords.verifyResponseStatusCode(user_response, 200)

def login_request = new RequestObject()
login_request.setRestUrl("https://petstore.swagger.io/v2/user/login?username=TestUser&password=")
login_request.setRestRequestMethod("GET")
addAuthHeader(login_request)

def login_response = WSBuiltInKeywords.sendRequest(login_request)
WSBuiltInKeywords.verifyResponseStatusCode(login_response, 400)

assertThat(login_response.getStatusCode()).isEqualTo(400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
