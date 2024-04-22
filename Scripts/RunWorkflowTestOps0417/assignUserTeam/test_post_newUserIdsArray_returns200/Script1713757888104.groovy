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

// Step 1: Create a new Team
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "Team1__unique__", "role": "OWNER", "users": []])))
teamRequest.setBodyContent(teamPayload)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2: Extract the teamId
teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 3: Create a new User
def userRequest = new RequestObject()
userRequest.setRestUrl("https://testops.katalon.io/api/v1/users")
userRequest.setRestRequestMethod("POST")
addAuthHeader(userRequest)
addContentTypeHeader(userRequest)
def userPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["firstName": "John", "lastName": "Doe"])))
userRequest.setBodyContent(userPayload)
def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
def userId = new JsonSlurper().parseText(userResponse.getResponseText())["id"]

// Step 4: Extract the userId
userId = new JsonSlurper().parseText(userResponse.getResponseText())["id"]

// Step 5: Add the User to the Team
def addUserRequest = new RequestObject()
addUserRequest.setRestUrl("https://testops.katalon.io/api/v1/users/add")
addUserRequest.setRestRequestMethod("POST")
addAuthHeader(addUserRequest)
addContentTypeHeader(addUserRequest)
def addUserPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["teamId": teamId, "newUserIds": [userId]])))
addUserRequest.setBodyContent(addUserPayload)
def addUserResponse = WSBuiltInKeywords.sendRequest(addUserRequest)

// Step 6: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(addUserResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
