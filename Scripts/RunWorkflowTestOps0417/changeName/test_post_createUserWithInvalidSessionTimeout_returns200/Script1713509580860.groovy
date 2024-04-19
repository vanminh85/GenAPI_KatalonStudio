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

def orgRequest = new RequestObject()
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgPayload = '{"name": "OrgName__unique__", "role": "OWNER", "quotaKSE": 10, "quotaEngine": 5, "usedKSE": 2, "usedEngine": 1}'
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = '{"name": "TeamName__unique__", "role": "OWNER", "organizationId": ' + orgResponse.getResponseText() + '}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"name": "ProjectName__unique__", "teamId": ' + teamResponse.getResponseText() + '}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def configRequest = new RequestObject()
configRequest.setRestUrl("https://testops.katalon.io/api/v1/configs")
configRequest.setRestRequestMethod("POST")
addAuthHeader(configRequest)
addContentTypeHeader(configRequest)
def configPayload = '{"webSocketUrl": "ws://example.com", "storeUrl": "http://example.com", "profiles": ["profile1", "profile2"]}'
configRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(configPayload)))
def configResponse = WSBuiltInKeywords.sendRequest(configRequest)
WSBuiltInKeywords.verifyResponseStatusCode(configResponse, 200)

def userRequest = new RequestObject()
userRequest.setRestUrl("https://testops.katalon.io/api/v1/users")
userRequest.setRestRequestMethod("POST")
addAuthHeader(userRequest)
addContentTypeHeader(userRequest)
def userPayload = '{"email": "test@example.com", "firstName": "John", "lastName": "Doe", "password": "password123", "configs": ' + configResponse.getResponseText() + ', "projects": [' + projectResponse.getResponseText() + '], "teams": [' + teamResponse.getResponseText() + '], "organizations": [' + orgResponse.getResponseText() + '], "organizationFeature": [], "trialExpirationDate": "2023-12-31T23:59:59Z", "systemRole": "USER", "surveyStatus": "NOT_SUBMITTED", "sessionTimeout": "invalid_value", "businessUser": true, "canCreateOfflineKSE": true, "canCreateOfflineRE": true, "samlSSO": false, "createdAt": "2023-01-01T00:00:00Z", "verified": false, "fullName": "John Doe"}'
userRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

