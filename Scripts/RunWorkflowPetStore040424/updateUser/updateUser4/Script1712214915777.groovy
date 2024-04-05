import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

// Import GlobalVariable
import internal.GlobalVariable

// Define addAuthHeader function
def addAuthHeader(request) {
	def authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		def auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

// Define addContentTypeHeader function
def addContentTypeHeader(request) {
	def content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

// Define uuid variable
uuid = UUID.randomUUID().toString()

// Step 1: Create a new User with a unique username
username1 = "test_user1__unique__"
payload1 = [
	"id": 1,
	"username": username1,
	"firstName": "John",
	"lastName": "Doe",
	"email": "john.doe@example.com",
	"password": "password123",
	"phone": "1234567890",
	"userStatus": 1
]
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(payload1))))
request1.setRestUrl("${GlobalVariable.base_url}/user")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Send a POST request to /user/{username} with the created username
request2 = new RequestObject()
request2.setRestUrl("${GlobalVariable.base_url}/user/${username1}")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3: Create another User with the same username
username2 = username1
payload2 = [
	"id": 2,
	"username": username2,
	"firstName": "Jane",
	"lastName": "Smith",
	"email": "jane.smith@example.com",
	"password": "password456",
	"phone": "9876543210",
	"userStatus": 1
]
request3 = new RequestObject()
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(payload2))))
request3.setRestUrl("${GlobalVariable.base_url}/user")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

// Step 4: Send a POST request to /user/{username} with the same username
request4 = new RequestObject()
request4.setRestUrl("${GlobalVariable.base_url}/user/${username2}")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 400)

// Step 5: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(response4, 400)

println("Test case executed successfully.")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
