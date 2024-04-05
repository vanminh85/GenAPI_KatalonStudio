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

// Step 1
def categoryPayload = '{"id": 2, "name": "Test Category"}'
def categoryRequest = new RequestObject()
categoryRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(categoryPayload)))
categoryRequest.setRestUrl("https://petstore.swagger.io/v2/category")
categoryRequest.setRestRequestMethod("POST")
addAuthHeader(categoryRequest)
addContentTypeHeader(categoryRequest)
def categoryResponse = WSBuiltInKeywords.sendRequest(categoryRequest)
WSBuiltInKeywords.verifyResponseStatusCode(categoryResponse, 200)

// Step 2
def petPayload = '{"id": 2, "category": {"id": 2, "name": "Test Category"}, "photoUrls": ["url1", "url2"], "status": "available"}'
def petRequest = new RequestObject()
petRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(petPayload)))
petRequest.setRestUrl("https://petstore.swagger.io/v2/pet")
petRequest.setRestRequestMethod("POST")
addAuthHeader(petRequest)
addContentTypeHeader(petRequest)
def petResponse = WSBuiltInKeywords.sendRequest(petRequest)
WSBuiltInKeywords.verifyResponseStatusCode(petResponse, 200)

// Step 3
def orderPayload = '{"id": 2, "petId": 2, "quantity": 1, "shipDate": "2022-01-01T00:00:00Z", "status": "placed", "complete": false}'
def orderRequest = new RequestObject()
orderRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orderPayload)))
orderRequest.setRestUrl("https://petstore.swagger.io/v2/store/order")
orderRequest.setRestRequestMethod("POST")
addAuthHeader(orderRequest)
addContentTypeHeader(orderRequest)
def orderResponse = WSBuiltInKeywords.sendRequest(orderRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orderResponse, 200)

// Step 4
def userPayload = '[{"id": 2, "username": "testuser2", "firstName": "Test", "lastName": "User", "email": "testuser2@example.com", "password": "password123", "phone": "1234567890"}]'
def userRequest = new RequestObject()
userRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
userRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithList")
userRequest.setRestRequestMethod("POST")
addAuthHeader(userRequest)
addContentTypeHeader(userRequest)
def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
