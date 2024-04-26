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
team_payload = '{"name": "TestTeam__unique__", "role": "OWNER", "users": [], "organizationId": 1}'
team_request = new RequestObject()
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
team_request.setRestUrl("https://testops.katalon.io/api/v1/teams")
team_request.setRestRequestMethod("POST")
addAuthHeader(team_request)
addContentTypeHeader(team_request)
team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)

team_id = new JsonSlurper().parseText(team_response.getResponseText())["id"]

// Step 2: Create a new Project
project_payload = '{"name": "TestProject__unique__", "teamId": ' + team_id + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
project_request = new RequestObject()
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

project_id = new JsonSlurper().parseText(project_response.getResponseText())["id"]

// Step 3: Create a new Test Project
test_project_payload = '{"name": "SampleGitTestProject__unique__", "description": "Test project description", "defaultTestProject": true, "uploadFileId": 1, "projectId": ' + project_id + ', "teamId": ' + team_id + ', "type": "GIT"}'
test_project_request = new RequestObject()
test_project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(test_project_payload)))
test_project_request.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample-git-test-project")
test_project_request.setRestRequestMethod("POST")
addAuthHeader(test_project_request)
addContentTypeHeader(test_project_request)
test_project_response = WSBuiltInKeywords.sendRequest(test_project_request)
WSBuiltInKeywords.verifyResponseStatusCode(test_project_response, 200)

test_project_id = new JsonSlurper().parseText(test_project_response.getResponseText())["id"]

// Step 4: Retrieve the Test Project detail
get_test_project_request = new RequestObject()
get_test_project_request.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + test_project_id)
get_test_project_request.setRestRequestMethod("GET")
addAuthHeader(get_test_project_request)
get_test_project_response = WSBuiltInKeywords.sendRequest(get_test_project_request)
WSBuiltInKeywords.verifyResponseStatusCode(get_test_project_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

