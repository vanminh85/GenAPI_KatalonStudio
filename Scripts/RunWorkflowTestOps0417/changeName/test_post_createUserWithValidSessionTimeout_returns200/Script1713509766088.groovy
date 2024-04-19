import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
	if (authToken) {
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new OrganizationResource
orgPayload = '{"name": "OrgName__unique__", "role": "OWNER", "quotaKSE": 10, "quotaEngine": 10, "usedKSE": 0, "usedEngine": 0}'
orgRequest = new RequestObject()
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

// Step 2: Create a new TeamResource
teamPayload = '{"name": "TeamName__unique__", "role": "OWNER", "organizationId": ' + orgResponse.getResponseText() + '}'
teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 3: Create a new TestProjectResource
projectPayload = '{"name": "ProjectName__unique__", "teamId": ' + teamResponse.getResponseText() + '}'
projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 4: Create a new ConfigResource
configPayload = '{"webSocketUrl": "ws://example.com", "storeUrl": "http://example.com", "profiles": ["profile1", "profile2"]}'
configRequest = new RequestObject()
configRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(configPayload)))
configRequest.setRestUrl("https://testops.katalon.io/api/v1/configs")
configRequest.setRestRequestMethod("POST")
addAuthHeader(configRequest)
addContentTypeHeader(configRequest)
configResponse = WSBuiltInKeywords.sendRequest(configRequest)
WSBuiltInKeywords.verifyResponseStatusCode(configResponse, 200)

// Step 5: Create a new UserResource
userPayload = '{"sessionTimeout": 3600}'
userRequest = new RequestObject()
userRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
userRequest.setRestUrl("https://testops.katalon.io/api/v1/users")
userRequest.setRestRequestMethod("POST")
addAuthHeader(userRequest)
addContentTypeHeader(userRequest)
userResponse = WSBuiltInKeywords.sendRequest(userRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

