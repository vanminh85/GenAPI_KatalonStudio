import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

import static com.kms.katalon.core.testobject.ConditionType.EQUALS

// Step 1: Create an existing User resource with a unique username
def user_payload = [
	"id": 1,
	"username": "test_user__unique__",
	"firstName": "John",
	"lastName": "Doe",
	"email": "johndoe@example.com",
	"password": "password123",
	"phone": "1234567890",
	"userStatus": 1
]

def request1 = new RequestObject()
request1.setRestUrl("https://petstore.swagger.io/v2/user/createWithList")
request1.setRestRequestMethod("POST")
addContentTypeHeader(request1)
addAuthHeader(request1)
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))

def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Send a POST request to /user/{username} with the existing username
def username = "test_user__unique__"
def request2 = new RequestObject()
request2.setRestUrl("https://petstore.swagger.io/v2/user/${username}")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)

def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 400)

// Step 3: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(response2, 400)

println("Test case passed successfully.")

def addAuthHeader(request) {
	def authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
	if (authToken) {
		def auth_header = new TestObjectProperty("authorization", EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	def content_type_header = new TestObjectProperty("content-type", EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
