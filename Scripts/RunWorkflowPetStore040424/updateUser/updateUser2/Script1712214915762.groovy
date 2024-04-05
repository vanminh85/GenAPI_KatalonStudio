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
	"username": "invalid_username__unique__",
	"firstName": "John",
	"lastName": "Doe",
	"email": "johndoe@example.com",
	"password": "password123",
	"phone": "1234567890",
	"userStatus": 1
]

def createWithArrayRequest = new RequestObject()
createWithArrayRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))
createWithArrayRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
createWithArrayRequest.setRestRequestMethod("POST")
addAuthHeader(createWithArrayRequest)
addContentTypeHeader(createWithArrayRequest)

def createWithArrayResponse = WSBuiltInKeywords.sendRequest(createWithArrayRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createWithArrayResponse, 200)

def getUserRequest = new RequestObject()
getUserRequest.setRestUrl("https://petstore.swagger.io/v2/user/${user_payload.username}")
getUserRequest.setRestRequestMethod("POST")
addAuthHeader(getUserRequest)

def getUserResponse = WSBuiltInKeywords.sendRequest(getUserRequest)
WSBuiltInKeywords.verifyResponseStatusCode(getUserResponse, 404)

assert getUserResponse.getStatusCode() == 404

println("Test case passed: test_post_invalidUsername_returns404")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
