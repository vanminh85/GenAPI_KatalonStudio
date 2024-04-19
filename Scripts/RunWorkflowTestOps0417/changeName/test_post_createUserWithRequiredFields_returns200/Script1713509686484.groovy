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
org_payload = '{"name": "OrgName__unique__", "role": "OWNER", "quotaKSE": 100, "quotaEngine": 100, "usedKSE": 0, "usedEngine": 0}'
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(org_payload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/organizations")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2
team_payload = '{"name": "TeamName__unique__", "role": "OWNER", "organizationId": ' + response1.getResponseText() + '.id}'
request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/teams")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3
project_payload = '{"name": "ProjectName__unique__", "teamId": ' + response2.getResponseText() + '.id}'
request3 = new RequestObject()
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
request3.setRestUrl("https://testops.katalon.io/api/v1/projects")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

// Step 4
config_payload = '{"webSocketUrl": "ws://example.com", "storeUrl": "http://store.com", "profiles": ["profile1", "profile2"]}'
request4 = new RequestObject()
request4.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(config_payload)))
request4.setRestUrl("https://testops.katalon.io/api/v1/configs")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 200)

// Step 5
user_payload = '{"email": "test@example.com", "firstName": "John", "lastName": "Doe", "password": "password123", "trialExpirationDate": "2023-12-31T23:59:59Z", "systemRole": "USER", "sessionTimeout": 3600, "businessUser": true}'
request5 = new RequestObject()
request5.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(user_payload)))
request5.setRestUrl("https://testops.katalon.io/api/v1/users")
request5.setRestRequestMethod("POST")
addAuthHeader(request5)
addContentTypeHeader(request5)
response5 = WSBuiltInKeywords.sendRequest(request5)
WSBuiltInKeywords.verifyResponseStatusCode(response5, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

