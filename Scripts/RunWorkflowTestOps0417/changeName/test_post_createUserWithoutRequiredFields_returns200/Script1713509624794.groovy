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

// Step 1: Create a new OrganizationResource
org_payload = '{"name": "OrgName__unique__", "role": "OWNER", "quotaKSE": 10, "quotaEngine": 5, "usedKSE": 0, "usedEngine": 0}'
org_request = new RequestObject()
org_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(org_payload)))
org_request.setRestUrl("https://testops.katalon.io/api/v1/organizations")
org_request.setRestRequestMethod("POST")
addAuthHeader(org_request)
addContentTypeHeader(org_request)
org_response = WSBuiltInKeywords.sendRequest(org_request)
WSBuiltInKeywords.verifyResponseStatusCode(org_response, 200)

// Step 2: Create a new TeamResource
team_payload = '{"name": "TeamName__unique__", "role": "OWNER", "organizationId": ' + org_response.getResponseText() + '.id}'
team_request = new RequestObject()
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
team_request.setRestUrl("https://testops.katalon.io/api/v1/teams")
team_request.setRestRequestMethod("POST")
addAuthHeader(team_request)
addContentTypeHeader(team_request)
team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)

// Step 3: Create a new TestProjectResource
project_payload = '{"name": "ProjectName__unique__", "teamId": ' + team_response.getResponseText() + '.id}'
project_request = new RequestObject()
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

// Step 4: Create a new ConfigResource
config_payload = '{"webSocketUrl": "ws://example.com", "storeUrl": "http://example.com", "profiles": ["profile1", "profile2"]}'
config_request = new RequestObject()
config_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(config_payload)))
config_request.setRestUrl("https://testops.katalon.io/api/v1/configs")
config_request.setRestRequestMethod("POST")
addAuthHeader(config_request)
addContentTypeHeader(config_request)
config_response = WSBuiltInKeywords.sendRequest(config_request)
WSBuiltInKeywords.verifyResponseStatusCode(config_response, 200)

// Step 5: Create a new UserResource without required fields
user_payload = '{"email": "test@example.com", "firstName": "John", "lastName": "Doe"}'
user_request = new RequestObject()
user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(user_payload)))
user_request.setRestUrl("https://testops.katalon.io/api/v1/users")
user_request.setRestRequestMethod("POST")
addAuthHeader(user_request)
addContentTypeHeader(user_request)
user_response = WSBuiltInKeywords.sendRequest(user_request)
WSBuiltInKeywords.verifyResponseStatusCode(user_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

