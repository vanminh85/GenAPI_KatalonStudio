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
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TeamName__unique__", "role": "OWNER"}')))

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = (new JsonSlurper().parseText(teamResponse.getResponseText()))['id']

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "ProjectName__unique__", "teamId": ' + teamId + ', "timezone": "UTC", "status": "ACTIVE"}')))

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = (new JsonSlurper().parseText(projectResponse.getResponseText()))['id']

def gitRepoRequest = new RequestObject()
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")
addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)
gitRepoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"testProjectId": ' + projectId + ', "name": "GitRepoName__unique__", "repository": "https://github.com/example", "branch": "main", "username": "username", "password": "password", "accessKeyId": "accessKeyId", "secretAccessKey": "secretAccessKey", "projectId": 1, "teamId": ' + teamId + '}')))

def gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
WSBuiltInKeywords.verifyResponseStatusCode(gitRepoResponse, 200)

def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TestProjectName__unique__", "description": "TestProjectDescription", "defaultTestProject": true, "uploadFileId": 1, "projectId": ' + projectId + ', "teamId": ' + teamId + ', "type": "KS", "gitRepository": {"id": ' + projectId + ', "name": "GitRepoName__unique__"}}')))

def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

