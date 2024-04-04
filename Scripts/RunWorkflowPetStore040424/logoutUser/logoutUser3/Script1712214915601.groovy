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

def user_payload = [
	"id": 1,
	"username": "test_user__unique__",
	"firstName": "John",
	"lastName": "Doe",
	"email": "johndoe@example.com",
	"password": "password",
	"phone": "1234567890",
	"userStatus": 1
]

def login_payload = [
	"username": "test_user__unique__",
	"password": "password"
]

def logout_payload = [
	"token": "invalid_token"
]

def createUserRequest = new RequestObject()
createUserRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))
createUserRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
createUserRequest.setRestRequestMethod("POST")
addContentTypeHeader(createUserRequest)
addAuthHeader(createUserRequest)

def loginRequest = new RequestObject()
loginRequest.setRestUrl("https://petstore.swagger.io/v2/user/login")
loginRequest.setRestRequestMethod("GET")
loginRequest.setRestParameters([
	new TestObjectProperty("username", ConditionType.EQUALS, login_payload.username),
	new TestObjectProperty("password", ConditionType.EQUALS, login_payload.password)
])
addAuthHeader(loginRequest)

def logoutRequest = new RequestObject()
logoutRequest.setRestUrl("https://petstore.swagger.io/v2/user/logout")
logoutRequest.setRestRequestMethod("GET")
logoutRequest.setRestParameters([
	new TestObjectProperty("token", ConditionType.EQUALS, logout_payload.token)
])
addAuthHeader(logoutRequest)

def createUserResponse = WSBuiltInKeywords.sendRequest(createUserRequest)
assert WSBuiltInKeywords.verifyResponseStatusCode(createUserResponse, 200)

def loginResponse = WSBuiltInKeywords.sendRequest(loginRequest)
assert WSBuiltInKeywords.verifyResponseStatusCode(loginResponse, 200)

def logoutResponse = WSBuiltInKeywords.sendRequest(logoutRequest)
assert WSBuiltInKeywords.verifyResponseStatusCode(logoutResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
