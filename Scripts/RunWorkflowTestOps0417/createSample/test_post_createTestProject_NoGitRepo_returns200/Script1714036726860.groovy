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
team_payload = '{"name": "TeamName__unique__", "role": "OWNER"}'
team_request = new RequestObject()
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
team_request.setRestUrl("https://testops.katalon.io/api/v1/teams")
team_request.setRestRequestMethod("POST")
addAuthHeader(team_request)
addContentTypeHeader(team_request)
team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)

// Step 2: Create a new Project
project_payload = '{"name": "ProjectName__unique__", "teamId": 1, "timezone": "UTC", "status": "ACTIVE"}'
project_request = new RequestObject()
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

// Step 3: Create a new Git Repository
git_repo_payload = '{"testProjectId": 1, "name": "GitRepoName__unique__", "repository": "https://github.com/example.git", "branch": "main", "username": "username", "password": "password", "accessKeyId": "accessKeyId", "secretAccessKey": "secretAccessKey", "projectId": 1, "teamId": 1}'
git_repo_request = new RequestObject()
git_repo_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(git_repo_payload)))
git_repo_request.setRestUrl("https://testops.katalon.io/api/v1/git/create")
git_repo_request.setRestRequestMethod("POST")
addAuthHeader(git_repo_request)
addContentTypeHeader(git_repo_request)
git_repo_response = WSBuiltInKeywords.sendRequest(git_repo_request)
WSBuiltInKeywords.verifyResponseStatusCode(git_repo_response, 200)

// Step 4: Create a new Test Project
test_project_payload = '{"name": "TestProjectName__unique__", "description": "TestProjectDescription", "defaultTestProject": true, "uploadFileId": 1, "projectId": 1, "teamId": 1, "type": "GIT", "gitRepository": {"id": 1}}'
test_project_request = new RequestObject()
test_project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(test_project_payload)))
test_project_request.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample")
test_project_request.setRestRequestMethod("POST")
addAuthHeader(test_project_request)
addContentTypeHeader(test_project_request)
test_project_response = WSBuiltInKeywords.sendRequest(test_project_request)
WSBuiltInKeywords.verifyResponseStatusCode(test_project_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

