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

def createWithArrayRequest = new RequestObject()
def createWithArrayPayload = '{"id": 1, "username": "test_user__unique__", "userStatus": 1}'
createWithArrayRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createWithArrayPayload)))
createWithArrayRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
createWithArrayRequest.setRestRequestMethod("POST")
addAuthHeader(createWithArrayRequest)
addContentTypeHeader(createWithArrayRequest)
def createWithArrayResponse = WSBuiltInKeywords.sendRequest(createWithArrayRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createWithArrayResponse, 200)

def loginRequest = new RequestObject()
def loginPayload = '{"username": "test_user__unique__", "password": "test_password"}'
loginRequest.setRestUrl("https://petstore.swagger.io/v2/user/login")
loginRequest.setRestRequestMethod("GET")
loginRequest.getRestParameters().add(new TestObjectProperty("username", ConditionType.EQUALS, "test_user__unique__"))
loginRequest.getRestParameters().add(new TestObjectProperty("password", ConditionType.EQUALS, "test_password"))
addAuthHeader(loginRequest)
def loginResponse = WSBuiltInKeywords.sendRequest(loginRequest)
WSBuiltInKeywords.verifyResponseStatusCode(loginResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
