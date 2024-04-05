import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

import static org.assertj.core.api.Assertions.*

// Step 1: Create a new User with a valid username
String username = "test_user__unique__"
def user_payload = [
	"id": 1,
	"username": username,
	"firstName": "John",
	"lastName": "Doe",
	"email": "johndoe@example.com",
	"password": "password",
	"phone": "1234567890",
	"userStatus": 1
]

RequestObject request1 = new RequestObject()
request1.setRestUrl("https://petstore.swagger.io/v2/user/createWithArray")
request1.setRestRequestMethod("POST")
addContentTypeHeader(request1)
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))

addAuthHeader(request1)

ResponseObject response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Send a POST request to /user/{username} with the created username using unauthorized credentials
RequestObject request2 = new RequestObject()
request2.setRestUrl("https://petstore.swagger.io/v2/user/" + username)
request2.setRestRequestMethod("POST")
addContentTypeHeader(request2)
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(user_payload))))

addAuthHeader(request2)

ResponseObject response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 401)

// Step 3: Verify that the response status code is 401
WSBuiltInKeywords.verifyResponseStatusCode(response2, 401)

println("Test case passed: test_post_unauthorizedUser_returns401")

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

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
