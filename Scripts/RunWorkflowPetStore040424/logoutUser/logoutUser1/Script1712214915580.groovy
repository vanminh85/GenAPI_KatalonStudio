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

def userPayload = [
	username: "test_user__unique__",
	userStatus: 1
]

def createUserRequest = new RequestObject()
createUserRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(userPayload))))
createUserRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
createUserRequest.setRestRequestMethod("POST")
addAuthHeader(createUserRequest)
addContentTypeHeader(createUserRequest)

def createUserResponse = WSBuiltInKeywords.sendRequest(createUserRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createUserResponse, 200)

def logoutRequest = new RequestObject()
logoutRequest.setRestUrl("https://petstore.swagger.io/v2/user/logout")
logoutRequest.setRestRequestMethod("POST")
addAuthHeader(logoutRequest)

def logoutResponse = WSBuiltInKeywords.sendRequest(logoutRequest)
WSBuiltInKeywords.verifyResponseStatusCode(logoutResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
