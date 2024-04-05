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
request1.setRestUrl("https://petstore.swagger.io/v2/category")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["id": 4, "name": "Test Category"]))))

def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def request2 = new RequestObject()
request2.setRestUrl("https://petstore.swagger.io/v2/pet")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["id": 4, "category": ["id": 4, "name": "Test Category"], "name": "Test Pet", "photoUrls": ["url1", "url2"], "status": "available"]))))

def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def request3 = new RequestObject()
request3.setRestUrl("https://petstore.swagger.io/v2/store/order")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["id": 4, "petId": 4, "quantity": 1, "shipDate": "2022-01-01T00:00:00Z", "status": "placed", "complete": false]))))

def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

def request4 = new RequestObject()
request4.setRestUrl("https://petstore.swagger.io/v2/user/createWithList")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
request4.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([["id": 4, "username": "testuser__unique__", "firstName": "Test", "lastName": "User", "email": "testuser4@example.com", "password": "password123", "phone": "1234567890", "userStatus": 1]])))

def response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
