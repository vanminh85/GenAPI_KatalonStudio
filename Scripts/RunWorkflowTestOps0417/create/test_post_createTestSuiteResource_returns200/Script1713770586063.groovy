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
orgPayload = '{"name": "Organization__unique__", "role": "OWNER", "domain": "example.com", "subdomainUrl": "example", "tier": "FREE", "requestedUserVerified": true}'
orgRequest = new RequestObject()
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

// Step 2: Create a new TeamResource
teamPayload = '{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + orgResponse.getResponseText() + '.id}'
teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 3: Create a new ProjectResource
projectPayload = '{"name": "Project__unique__", "teamId": ' + teamResponse.getResponseText() + '.id, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 4: Create a new TestProjectResource
testProjectPayload = '{"name": "TestProject__unique__", "description": "Test Project Description", "defaultTestProject": true, "uploadFileId": 123, "projectId": ' + projectResponse.getResponseText() + '.id, "teamId": ' + teamResponse.getResponseText() + '.teamId, "type": "KS"}'
testProjectRequest = new RequestObject()
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/upload-script-repo")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

// Step 5: Create a new TestSuiteResource
testSuitePayload = '{"name": "TestSuite__unique__", "path": "/tests", "project": ' + JsonOutput.toJson(projectPayload) + ', "testProject": ' + JsonOutput.toJson(testProjectPayload) + ', "user": {"id": 1, "name": "User"}, "type": "KATALON_STUDIO", "testFolder": {"id": 1, "name": "Folder"}, "urlId": "test-suite"}'
testSuiteRequest = new RequestObject()
testSuiteRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testSuitePayload)))
testSuiteRequest.setRestUrl("https://testops.katalon.io/api/v1/test-suites/create")
testSuiteRequest.setRestRequestMethod("POST")
addAuthHeader(testSuiteRequest)
addContentTypeHeader(testSuiteRequest)
testSuiteResponse = WSBuiltInKeywords.sendRequest(testSuiteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testSuiteResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

