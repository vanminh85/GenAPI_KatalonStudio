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

// Step 1: Create a new Team
team_request = new RequestObject()
team_request.setRestUrl("https://testops.katalon.io/api/v1/teams")
team_request.setRestRequestMethod("POST")
addAuthHeader(team_request)
addContentTypeHeader(team_request)
team_payload = '{"name": "TeamName__unique__", "role": "OWNER", "users": [], "organizationId": 1}'
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)

// Step 2: Create a new Project
project_request = new RequestObject()
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_payload = '{"name": "ProjectName__unique__", "teamId": 1, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

// Step 3: Create a new Test Project
test_project_request = new RequestObject()
test_project_request.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample-git-test-project")
test_project_request.setRestRequestMethod("POST")
addAuthHeader(test_project_request)
addContentTypeHeader(test_project_request)
test_project_payload = '{"name": "TestProjectName__unique__", "description": "Test Project Description", "defaultTestProject": true, "uploadFileId": 1, "projectId": 1, "teamId": 1, "type": "KS"}'
test_project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(test_project_payload)))
test_project_response = WSBuiltInKeywords.sendRequest(test_project_request)
WSBuiltInKeywords.verifyResponseStatusCode(test_project_response, 200)

// Step 4: Update the Test Project detail
test_project_id = 1
updated_test_project_request = new RequestObject()
updated_test_project_request.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + test_project_id)
updated_test_project_request.setRestRequestMethod("PUT")
addAuthHeader(updated_test_project_request)
addContentTypeHeader(updated_test_project_request)
updated_test_project_payload = '{"name": "UpdatedTestProjectName", "description": "Updated Test Project Description", "defaultTestProject": false, "uploadFileId": 2, "projectId": 2, "teamId": 2, "type": "GIT"}'
updated_test_project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updated_test_project_payload)))
updated_test_project_response = WSBuiltInKeywords.sendRequest(updated_test_project_request)
WSBuiltInKeywords.verifyResponseStatusCode(updated_test_project_response, 200)

// Step 5: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(updated_test_project_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

