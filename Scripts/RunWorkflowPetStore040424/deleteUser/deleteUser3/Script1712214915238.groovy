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

// Step 1: Create a new Category
def categoryRequest = new RequestObject()
categoryRequest.setRestUrl("https://petstore.swagger.io/v2/category")
categoryRequest.setRestRequestMethod("POST")
addAuthHeader(categoryRequest)
addContentTypeHeader(categoryRequest)
def categoryPayload = '{"id": 1, "name": "category__unique__"}'
categoryRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(categoryPayload)))
def categoryResponse = WSBuiltInKeywords.sendRequest(categoryRequest)
WSBuiltInKeywords.verifyResponseStatusCode(categoryResponse, 200)

// Step 2: Create a new Pet
def petRequest = new RequestObject()
petRequest.setRestUrl("https://petstore.swagger.io/v2/pet")
petRequest.setRestRequestMethod("POST")
addAuthHeader(petRequest)
addContentTypeHeader(petRequest)
def petPayload = '{"id": 1, "category": {"id": 1, "name": "category__unique__"}, "name": "pet__unique__", "photoUrls": ["url"]}'
petRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(petPayload)))
def petResponse = WSBuiltInKeywords.sendRequest(petRequest)
WSBuiltInKeywords.verifyResponseStatusCode(petResponse, 200)

// Step 3: Create a new Order
def orderRequest = new RequestObject()
orderRequest.setRestUrl("https://petstore.swagger.io/v2/store/order")
orderRequest.setRestRequestMethod("POST")
addAuthHeader(orderRequest)
addContentTypeHeader(orderRequest)
def orderPayload = '{"id": 1, "petId": 1, "quantity": 1, "shipDate": "2022-01-01T00:00:00Z", "status": "placed", "complete": true}'
orderRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orderPayload)))
def orderResponse = WSBuiltInKeywords.sendRequest(orderRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orderResponse, 200)

// Step 4: Create a new User
def userRequest = new RequestObject()
userRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
userRequest.setRestRequestMethod("POST")
addAuthHeader(userRequest)
addContentTypeHeader(userRequest)
def userPayload = '[{"id": 1, "username": "user__unique__", "userStatus": 1}]'
userRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userResponse, 200)

// Step 5: Update the created User
def updatedUserRequest = new RequestObject()
updatedUserRequest.setRestUrl("https://petstore.swagger.io/v2/user/user__unique__")
updatedUserRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedUserRequest)
addContentTypeHeader(updatedUserRequest)
def updatedUserPayload = '{"id": 1, "username": "user__unique__updated", "userStatus": 2}'
updatedUserRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedUserPayload)))
def updatedUserResponse = WSBuiltInKeywords.sendRequest(updatedUserRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedUserResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
