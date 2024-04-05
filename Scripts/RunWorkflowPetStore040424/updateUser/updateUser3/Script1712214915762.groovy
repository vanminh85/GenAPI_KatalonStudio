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
	"username": "__unique__",
	"firstName": "__unique__",
	"lastName": "__unique__",
	"email": "__unique__",
	"password": "__unique__",
	"phone": "__unique__",
	"userStatus": 1
]

def createRequest = new RequestObject()
createRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))
createRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
createRequest.setRestRequestMethod("POST")
addAuthHeader(createRequest)
addContentTypeHeader(createRequest)

def createResponse = WSBuiltInKeywords.sendRequest(createRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createResponse, 200)

def username = user_payload["username"]

def getRequest = new RequestObject()
getRequest.setRestUrl("https://petstore.swagger.io/v2/user/${username}")
getRequest.setRestRequestMethod("POST")
addAuthHeader(getRequest)
addContentTypeHeader(getRequest)

def getResponse = WSBuiltInKeywords.sendRequest(getRequest)
WSBuiltInKeywords.verifyResponseStatusCode(getResponse, 400)

WSBuiltInKeywords.verifyResponseStatusCode(getResponse, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
