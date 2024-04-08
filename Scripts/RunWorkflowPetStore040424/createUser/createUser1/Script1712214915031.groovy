import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1
request1 = new RequestObject()
request1.setRestUrl("https://petstore.swagger.io/v2/category")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
payload_category = '{"id": 1, "name": "Test Category"}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_category)))
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2
request2 = new RequestObject()
request2.setRestUrl("https://petstore.swagger.io/v2/pet")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
payload_pet = '{"id": 1, "category": {"id": 1, "name": "Test Category"}, "name": "Test Pet", "photoUrls": ["url1", "url2"], "status": "available"}'
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_pet)))
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3
request3 = new RequestObject()
request3.setRestUrl("https://petstore.swagger.io/v2/store/order")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
payload_order = '{"id": 1, "petId": 1, "quantity": 1, "shipDate": "2022-12-31T23:59:59Z", "status": "placed", "complete": true}'
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_order)))
response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

// Step 4
request4 = new RequestObject()
request4.setRestUrl("https://petstore.swagger.io/v2/user")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
payload_user = '{"id": 1, "username": "test_user", "firstName": "Test", "lastName": "User", "email": "test@example.com", "password": "password123", "phone": "1234567890", "userStatus": 1}'
request4.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_user)))
response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
