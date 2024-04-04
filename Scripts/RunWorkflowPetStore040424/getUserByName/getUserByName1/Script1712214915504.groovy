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
addContentTypeHeader(create_user_request)
def user_payload = [
	"id": 1,
	"username": "test_user__unique__",
	"firstName": "John",
	"lastName": "Doe",
	"email": "johndoe@example.com",
	"password": "password123",
	"phone": "1234567890",
	"userStatus": 1
]
create_user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))

WSBuiltInKeywords.sendRequest(create_user_request)

def post_url = "https://petstore.swagger.io/v2/user/${uuid}"
def post_request = new RequestObject()
post_request.setRestUrl(post_url)
post_request.setRestRequestMethod("POST")
addAuthHeader(post_request)

WSBuiltInKeywords.sendRequest(post_request)

def verify_response = WSBuiltInKeywords.verifyResponseStatusCode(post_request.getResponseObject(), 200)
assert verify_response

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
