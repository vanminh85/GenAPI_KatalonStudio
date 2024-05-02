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

// Step 1
def createTestProjectRequest = new RequestObject()
createTestProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample-git-test-project")
createTestProjectRequest.setRestRequestMethod("POST")
addAuthHeader(createTestProjectRequest)
addContentTypeHeader(createTestProjectRequest)

def createTestProjectPayload = '{"name": "TestProject__unique__", "description": "Test Project Description", "type": "GIT", "gitRepository": {"id": 1, "testProjectId": 1, "name": "TestRepo__unique__", "repository": "https://github.com/testrepo", "branch": "main", "username": "testuser", "password": "testpassword", "accessKeyId": "accesskey", "secretAccessKey": "secretkey", "projectId": 1, "teamId": 1, "createdAt": "2022-01-01T00:00:00Z", "updatedAt": "2022-01-01T00:00:00Z", "description": "Test Repository Description", "vcsType": "GITHUB", "shouldMergeTestResultsForNewScriptRepo": true}}'
createTestProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createTestProjectPayload)))

def createTestProjectResponse = WSBuiltInKeywords.sendRequest(createTestProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createTestProjectResponse, 200)

// Step 2
def createTestProjectPublishRequest = new RequestObject()
createTestProjectPublishRequest.setRestUrl("https://testops.katalon.io/api/v1/test-management/test-projects/1/publish")
createTestProjectPublishRequest.setRestRequestMethod("POST")
addAuthHeader(createTestProjectPublishRequest)
addContentTypeHeader(createTestProjectPublishRequest)

def createTestProjectPublishPayload = '{"message": "Test Project Publish Message", "testCases": []}'
createTestProjectPublishRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createTestProjectPublishPayload)))

def createTestProjectPublishResponse = WSBuiltInKeywords.sendRequest(createTestProjectPublishRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createTestProjectPublishResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

