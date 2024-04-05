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
def request1 = new RequestObject()
request1.setRestUrl("https://petstore.swagger.io/v2/category")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def bodyContent1 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["id": 3, "name": "Test Category"])))
request1.setBodyContent(bodyContent1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2
def request2 = new RequestObject()
request2.setRestUrl("https://petstore.swagger.io/v2/pet")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def bodyContent2 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["id": 3, "category": ["id": 3, "name": "Test Category"], "name": "Test Pet", "photoUrls": ["url1", "url2"], "status": "available"])))
request2.setBodyContent(bodyContent2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3
def request3 = new RequestObject()
request3.setRestUrl("https://petstore.swagger.io/v2/store/order")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
def bodyContent3 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["id": 3, "petId": 3, "quantity": 1, "shipDate": "2022-01-01T00:00:00Z", "status": "placed", "complete": false])))
request3.setBodyContent(bodyContent3)
def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

// Step 4
def request4 = new RequestObject()
request4.setRestUrl("https://petstore.swagger.io/v2/user/createWithList")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
def bodyContent4 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([["id": 3, "username": "testuser3__unique__", "firstName": "Test", "lastName": "User", "email": "testuser3@example.com", "password": "password123", "phone": "1234567890", "userStatus": "active"]])))
request4.setBodyContent(bodyContent4)
def response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
