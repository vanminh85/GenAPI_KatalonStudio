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

// Step 1
def gitRepoData = '{"name": "TestRepo__unique__", "repository": "https://github.com/testrepo", "branch": "main", "username": "testuser", "password": "testpassword", "vcsType": "GITHUB", "shouldMergeTestResultsForNewScriptRepo": true}'
def gitRepoRequest = new RequestObject()
gitRepoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(gitRepoData)))
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")
addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)
def gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
WSBuiltInKeywords.verifyResponseStatusCode(gitRepoResponse, 200)

// Step 2
def gitRepoId = new JsonSlurper().parseText(gitRepoResponse.getResponseText())['id']
def testProjectData = '{"name": "TestProject__unique__", "description": "Test project description", "type": "GIT", "gitRepository": {"id": ' + gitRepoId + '}}'
def testProjectRequest = new RequestObject()
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectData)))
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample-git-test-project?projectId=1&type=API")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

// Step 3
def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())['id']
def publishRequest = new RequestObject()
publishRequest.setRestUrl("https://testops.katalon.io/api/v1/test-management/test-projects/" + testProjectId + "/publish")
publishRequest.setRestRequestMethod("POST")
addAuthHeader(publishRequest)
addContentTypeHeader(publishRequest)
def publishResponse = WSBuiltInKeywords.sendRequest(publishRequest)
WSBuiltInKeywords.verifyResponseStatusCode(publishResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
