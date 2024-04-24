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

// Request 1: Create a new Git Repository
def gitRepoRequest = new RequestObject()
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")

gitRepoPayload = '''
{
    "name": "TestRepo__unique__",
    "repository": "https://github.com/testrepo",
    "branch": "main",
    "username": "testuser",
    "password": "testpassword",
    "accessKeyId": "accesskey123",
    "secretAccessKey": "secretkey456",
    "projectId": 123,
    "teamId": 456,
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z",
    "description": "Test Git Repository",
    "vcsType": "GITHUB",
    "shouldMergeTestResultsForNewScriptRepo": true
}
'''
gitRepoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(gitRepoPayload)))

addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)

def gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
WSBuiltInKeywords.verifyResponseStatusCode(gitRepoResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}


