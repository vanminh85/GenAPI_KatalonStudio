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

def create_user_url = "https://petstore.swagger.io/v2/user/createWithList"
def create_user_request = new RequestObject()
create_user_request.setRestUrl(create_user_url)
create_user_request.setRestRequestMethod("POST")
addAuthHeader(create_user_request)
addContentTypeHeader(create_user_request)

def username = "test_user__unique__"
def user_payload = [
	"id": 1,
	"username": username,
	"firstName": "John",
	"lastName": "Doe",
	"email": "johndoe@example.com",
	"password": "password123",
	"phone": "1234567890",
	"userStatus": 1
]
def create_user_body = JsonOutput.toJson([user_payload])
create_user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(create_user_body)))

def create_user_response = WSBuiltInKeywords.sendRequest(create_user_request)
WSBuiltInKeywords.verifyResponseStatusCode(create_user_response, 200)

def invalid_payload = [
	"invalid_field": "invalid_value"
]
def update_user_url = "https://petstore.swagger.io/v2/user/${username}"
def update_user_request = new RequestObject()
update_user_request.setRestUrl(update_user_url)
update_user_request.setRestRequestMethod("POST")
addAuthHeader(update_user_request)
addContentTypeHeader(update_user_request)

def update_user_body = JsonOutput.toJson(invalid_payload)
update_user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(update_user_body)))

def update_user_response = WSBuiltInKeywords.sendRequest(update_user_request)
WSBuiltInKeywords.verifyResponseStatusCode(update_user_response, 400)
WSBuiltInKeywords.verifyResponseStatusCode(update_user_response, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
