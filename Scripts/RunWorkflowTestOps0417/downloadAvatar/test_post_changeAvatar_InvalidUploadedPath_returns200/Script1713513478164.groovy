import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
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

def userPayload = '{"email": "test@example.com", "firstName": "John", "lastName": "Doe", "password": "password123", "systemRole": "USER", "businessUser": false}'
def userRequest = new RequestObject()
userRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
userRequest.setRestUrl("https://testops.katalon.io/api/v1/users")
userRequest.setRestRequestMethod("POST")
addAuthHeader(userRequest)
addContentTypeHeader(userRequest)
def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userResponse, 200)

def avatarPayload = '{"uploadedPath": "/invalid/path"}'
def avatarRequest = new RequestObject()
avatarRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(avatarPayload)))
avatarRequest.setRestUrl("https://testops.katalon.io/api/v1/users/avatar")
avatarRequest.setRestRequestMethod("POST")
addAuthHeader(avatarRequest)
addContentTypeHeader(avatarRequest)
def avatarResponse = WSBuiltInKeywords.sendRequest(avatarRequest)
WSBuiltInKeywords.verifyResponseStatusCode(avatarResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
