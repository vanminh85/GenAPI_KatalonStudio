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
def teamPayload = '{"id": 1, "name": "Test Team__unique__", "role": "OWNER", "users": [], "organization": {}, "organizationId": 1}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2: Extract the teamId from the response

// Step 3: Create multiple new Users
def userPayloads = [
	'{"firstName": "User1__unique__", "lastName": "Lastname1__unique__"}',
	'{"firstName": "User2__unique__", "lastName": "Lastname2__unique__"}'
]

def userIds = []

for (userPayload in userPayloads) {
	def userRequest = new RequestObject()
	userRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
	userRequest.setRestUrl("https://testops.katalon.io/api/v1/users")
	userRequest.setRestRequestMethod("POST")
	addAuthHeader(userRequest)
	addContentTypeHeader(userRequest)
	def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
	userIds.add(new JsonSlurper().parseText(userResponse.getResponseText())["id"])
}

// Step 4: Extract the userIds from the responses

// Step 5: Make a POST request to /api/v1/users/add endpoint
def addUsersPayload = '{"teamId": ' + teamId + ', "newUserIds": ' + userIds + '}'
def addUsersRequest = new RequestObject()
addUsersRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(addUsersPayload)))
addUsersRequest.setRestUrl("https://testops.katalon.io/api/v1/users/add")
addUsersRequest.setRestRequestMethod("POST")
addAuthHeader(addUsersRequest)
addContentTypeHeader(addUsersRequest)
def addUsersResponse = WSBuiltInKeywords.sendRequest(addUsersRequest)

// Step 6: Verify that the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(addUsersResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

