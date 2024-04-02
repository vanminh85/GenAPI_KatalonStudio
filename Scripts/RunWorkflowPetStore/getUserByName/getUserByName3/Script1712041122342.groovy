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
	"firstName": "John",
	"lastName": "Doe",
	"email": "john.doe@example.com",
	"password": "password123",
	"phone": "1234567890",
	"userStatus": 1
]

def createWithListRequest = new RequestObject()
createWithListRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))
createWithListRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithList")
createWithListRequest.setRestRequestMethod("POST")
addAuthHeader(createWithListRequest)
addContentTypeHeader(createWithListRequest)

def createWithListResponse = WSBuiltInKeywords.sendRequest(createWithListRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createWithListResponse, 200)

def username = user_payload["username"]

def getUserRequest = new RequestObject()
getUserRequest.setRestUrl("https://petstore.swagger.io/v2/user/${username}")
getUserRequest.setRestRequestMethod("POST")
addAuthHeader(getUserRequest)
addContentTypeHeader(getUserRequest)

def getUserResponse = WSBuiltInKeywords.sendRequest(getUserRequest)
WSBuiltInKeywords.verifyResponseStatusCode(getUserResponse, 400)

WSBuiltInKeywords.verifyResponseStatusCode(getUserResponse, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
