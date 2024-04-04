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

// Step 1: Create a new Category resource
def category_payload = [
	"id": 1,
	"name": "TestCategory"
]

def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))
category_request.setRestUrl("https://petstore.swagger.io/v2/category")
category_request.setRestRequestMethod("POST")
addAuthHeader(category_request)
addContentTypeHeader(category_request)

def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

// Step 2: Create a new Order resource
def order_payload = [
	"id": 1,
	"petId": null,
	"quantity": 1,
	"shipDate": "2022-01-01T00:00:00Z",
	"status": "placed",
	"complete": false
]

def order_request = new RequestObject()
order_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(order_payload))))
order_request.setRestUrl("https://petstore.swagger.io/v2/store/order")
order_request.setRestRequestMethod("POST")
addAuthHeader(order_request)
addContentTypeHeader(order_request)

def order_response = WSBuiltInKeywords.sendRequest(order_request)
WSBuiltInKeywords.verifyResponseStatusCode(order_response, 200)

// Step 3: Send a POST request to /user/{username}
def user_payload = [
	"id": 1,
	"username": "TestUser",
	"firstName": "Test",
	"lastName": "User",
	"email": "testuser@example.com",
	"password": "password",
	"phone": "1234567890",
	"userStatus": 1
]

def user_request = new RequestObject()
user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(user_payload))))
user_request.setRestUrl("https://petstore.swagger.io/v2/user/${user_payload['username']}")
user_request.setRestRequestMethod("POST")
addAuthHeader(user_request)
addContentTypeHeader(user_request)

def user_response = WSBuiltInKeywords.sendRequest(user_request)
WSBuiltInKeywords.verifyResponseStatusCode(user_response, 400)

// Step 4: Verify the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(user_response, 400)

println("Test case executed successfully.")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

