import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

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

// Step 1: Create a new User resource with a unique username
def username = "test_user__unique__"
def user_payload = [
	"id": 0,
	"username": username,
	"firstName": "John",
	"lastName": "Doe",
	"email": "johndoe@example.com",
	"password": "password",
	"phone": "1234567890",
	"userStatus": 1
]
def create_user_url = "${GlobalVariable.base_url}/user/createWithArray"
def create_user_request = new RequestObject()
create_user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([user_payload]))))
create_user_request.setRestUrl(create_user_url)
create_user_request.setRestRequestMethod("POST")
addContentTypeHeader(create_user_request)
WSBuiltInKeywords.sendRequest(create_user_request)

// Step 2: Login the user using the /user/login API
def login_url = "${GlobalVariable.base_url}/user/login?username=${username}&password=password"
def login_request = new RequestObject()
login_request.setRestUrl(login_url)
login_request.setRestRequestMethod("GET")
addAuthHeader(login_request)
WSBuiltInKeywords.sendRequest(login_request)

// Step 3: Call the /user/logout API
def logout_url = "${GlobalVariable.base_url}/user/logout"
def logout_request = new RequestObject()
logout_request.setRestUrl(logout_url)
logout_request.setRestRequestMethod("GET")
addAuthHeader(logout_request)
WSBuiltInKeywords.sendRequest(logout_request)

// Step 4: Verify that the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(logout_request.getResponseObject(), 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

