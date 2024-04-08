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

def request1 = new RequestObject()
request1.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def payload1 = '[{"id": 1, "username": "test_user__unique__", "userStatus": 1}]'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def request2 = new RequestObject()
request2.setRestUrl("https://petstore.swagger.io/v2/user/login?username=test_user__unique__&password=incorrect_password")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
