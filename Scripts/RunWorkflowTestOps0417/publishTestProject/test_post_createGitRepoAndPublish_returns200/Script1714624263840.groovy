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
team_payload = '{"name": "Team1__unique__", "role": "OWNER"}'
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)
team_id = new JsonSlurper().parseText(team_response.getResponseText())["id"]

// Step 2: Create a new Project under the Team
project_request = new RequestObject()
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_payload = '{"name": "Project1__unique__", "teamId": ' + team_id + '}'
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)
project_id = new JsonSlurper().parseText(project_response.getResponseText())["id"]

// Step 3: Create a new Git Repository for the Test Project
git_repo_request = new RequestObject()
git_repo_request.setRestUrl("https://testops.katalon.io/api/v1/git/create")
git_repo_request.setRestRequestMethod("POST")
addAuthHeader(git_repo_request)
addContentTypeHeader(git_repo_request)
git_repo_payload = '{"testProjectId": ' + project_id + ', "name": "GitRepo1__unique__", "repository": "https://github.com/example/repo", "branch": "main"}'
git_repo_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(git_repo_payload)))
git_repo_response = WSBuiltInKeywords.sendRequest(git_repo_request)
WSBuiltInKeywords.verifyResponseStatusCode(git_repo_response, 200)

// Step 4: Publish the Test Project associated with the Git Repository
publish_request = new RequestObject()
publish_request.setRestUrl("https://testops.katalon.io/api/v1/test-management/test-projects/" + project_id + "/publish")
publish_request.setRestRequestMethod("POST")
addAuthHeader(publish_request)
addContentTypeHeader(publish_request)
publish_payload = '{"message": "Publishing test project", "testCases": [], "testSuites": []}'
publish_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(publish_payload)))
publish_response = WSBuiltInKeywords.sendRequest(publish_request)
WSBuiltInKeywords.verifyResponseStatusCode(publish_response, 200)

// Step 5: Verify that the response status code is 200
assert publish_response.getStatusCode() == 200

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

