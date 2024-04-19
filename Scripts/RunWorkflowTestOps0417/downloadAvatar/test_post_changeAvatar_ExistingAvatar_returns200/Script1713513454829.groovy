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

// Step 1: Create a new User
def userPayload = '{"email": "test@example.com", "firstName": "John", "lastName": "Doe", "password": "password123", "systemRole": "USER", "businessUser": false}'
def createUserRequest = new RequestObject()
createUserRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
createUserRequest.setRestUrl("https://testops.katalon.io/api/v1/users")
createUserRequest.setRestRequestMethod("POST")
addAuthHeader(createUserRequest)
addContentTypeHeader(createUserRequest)
def createUserResponse = WSBuiltInKeywords.sendRequest(createUserRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createUserResponse, 200)

// Step 2: Upload an avatar for the User
// Assume the avatar upload process here

// Step 3: Execute POST /api/v1/users/avatar with uploadedPath as a valid path
def uploadedPath = "/path/to/avatar.jpg"
def uploadAvatarRequest = new RequestObject()
uploadAvatarRequest.setRestUrl("https://testops.katalon.io/api/v1/users/avatar?uploadedPath=${uploadedPath}")
uploadAvatarRequest.setRestRequestMethod("POST")
addAuthHeader(uploadAvatarRequest)
def uploadAvatarResponse = WSBuiltInKeywords.sendRequest(uploadAvatarRequest)
WSBuiltInKeywords.verifyResponseStatusCode(uploadAvatarResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
