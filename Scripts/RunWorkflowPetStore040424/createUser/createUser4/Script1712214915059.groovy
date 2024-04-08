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

def categoryRequest = new RequestObject()
categoryRequest.setRestUrl("https://petstore.swagger.io/v2/category")
categoryRequest.setRestRequestMethod("POST")
addAuthHeader(categoryRequest)
addContentTypeHeader(categoryRequest)
def categoryData = '{"id": 1, "name": "Test Category"}'
categoryRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(categoryData)))
def categoryResponse = WSBuiltInKeywords.sendRequest(categoryRequest)
WSBuiltInKeywords.verifyResponseStatusCode(categoryResponse, 200)

def petRequest = new RequestObject()
petRequest.setRestUrl("https://petstore.swagger.io/v2/pet")
petRequest.setRestRequestMethod("POST")
addAuthHeader(petRequest)
addContentTypeHeader(petRequest)
def petData = '{"id": 1, "category": {"id": 1, "name": "Test Category"}, "name": "Test Pet", "photoUrls": ["url1", "url2"], "status": "available"}'
petRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(petData)))
def petResponse = WSBuiltInKeywords.sendRequest(petRequest)
WSBuiltInKeywords.verifyResponseStatusCode(petResponse, 200)

def orderRequest = new RequestObject()
orderRequest.setRestUrl("https://petstore.swagger.io/v2/store/order")
orderRequest.setRestRequestMethod("POST")
addAuthHeader(orderRequest)
addContentTypeHeader(orderRequest)
def orderData = '{"id": 1, "petId": 1, "quantity": 1, "shipDate": "2022-12-31T23:59:59Z", "status": "placed", "complete": true}'
orderRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orderData)))
def orderResponse = WSBuiltInKeywords.sendRequest(orderRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orderResponse, 200)

def invalidUserRequest = new RequestObject()
invalidUserRequest.setRestUrl("https://petstore.swagger.io/v2/user")
invalidUserRequest.setRestRequestMethod("POST")
addAuthHeader(invalidUserRequest)
addContentTypeHeader(invalidUserRequest)
def invalidUserData = '{"invalid_field": "value"}'
invalidUserRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(invalidUserData)))
def invalidUserResponse = WSBuiltInKeywords.sendRequest(invalidUserRequest)
WSBuiltInKeywords.verifyResponseStatusCode(invalidUserResponse, 405)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
