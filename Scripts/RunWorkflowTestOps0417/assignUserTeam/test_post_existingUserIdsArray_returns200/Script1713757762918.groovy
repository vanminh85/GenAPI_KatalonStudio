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

def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Team1__unique__"}')))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

def userRequest = new RequestObject()
userRequest.setRestUrl("https://testops.katalon.io/api/v1/users")
userRequest.setRestRequestMethod("POST")
addAuthHeader(userRequest)
addContentTypeHeader(userRequest)
userRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"firstName": "John__unique__", "lastName": "Doe__unique__"}')))
def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
def userId = new JsonSlurper().parseText(userResponse.getResponseText())["id"]

def addUserRequest = new RequestObject()
addUserRequest.setRestUrl("https://testops.katalon.io/api/v1/users/add")
addUserRequest.setRestRequestMethod("POST")
addAuthHeader(addUserRequest)
addContentTypeHeader(addUserRequest)
addUserRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"teamId": ' + teamId + ', "newUserIds": [' + userId + ']}')))
def addUserResponse = WSBuiltInKeywords.sendRequest(addUserRequest)
def addedUserId = new JsonSlurper().parseText(addUserResponse.getResponseText())[0]["id"]

def addAddedUserRequest = new RequestObject()
addAddedUserRequest.setRestUrl("https://testops.katalon.io/api/v1/users/add")
addAddedUserRequest.setRestRequestMethod("POST")
addAuthHeader(addAddedUserRequest)
addContentTypeHeader(addAddedUserRequest)
addAddedUserRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"teamId": ' + teamId + ', "newUserIds": [' + addedUserId + ']}')))
def addAddedUserResponse = WSBuiltInKeywords.sendRequest(addAddedUserRequest)

WSBuiltInKeywords.verifyResponseStatusCode(addAddedUserResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}