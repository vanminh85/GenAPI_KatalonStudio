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

// Step 1: Create a new Test Project
def createTestProjectRequest = new RequestObject()
createTestProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
createTestProjectRequest.setRestRequestMethod("POST")
addAuthHeader(createTestProjectRequest)
addContentTypeHeader(createTestProjectRequest)

def createTestProjectPayload = '{"name": "TestProject__unique__", "description": "Test Description", "defaultTestProject": true, "uploadFileId": 123, "projectId": 456, "teamId": 789, "createdAt": "2022-01-01T00:00:00Z", "latestJob": {"id": 1, "name": "Latest Job"}, "uploadFileName": "test_file.txt", "type": "KS", "gitRepository": {"id": 1, "name": "GitRepo__unique__"}, "testSuiteCollections": [{"id": 1, "name": "Test Suite Collection"}], "dirty": false, "autonomous": true}'
createTestProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createTestProjectPayload)))

def createTestProjectResponse = WSBuiltInKeywords.sendRequest(createTestProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createTestProjectResponse, 200)

// Step 2: Update the Test Project detail
def testProjectId = new JsonSlurper().parseText(createTestProjectResponse.getResponseText())['id']
def updateTestProjectRequest = new RequestObject()
updateTestProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${testProjectId}")
updateTestProjectRequest.setRestRequestMethod("PUT")
addAuthHeader(updateTestProjectRequest)
addContentTypeHeader(updateTestProjectRequest)

def updateTestProjectPayload = '{"name": "Updated TestProject__unique__", "description": "Updated Test Description", "defaultTestProject": false, "uploadFileId": 456, "projectId": 789, "teamId": 123, "createdAt": "2022-02-01T00:00:00Z", "latestJob": {"id": 2, "name": "New Latest Job"}, "uploadFileName": "updated_file.txt", "type": "GIT", "gitRepository": {"id": 2, "name": "NewGitRepo__unique__"}, "testSuiteCollections": [{"id": 2, "name": "New Test Suite Collection"}], "dirty": true, "autonomous": false}'
updateTestProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateTestProjectPayload)))

def updateTestProjectResponse = WSBuiltInKeywords.sendRequest(updateTestProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateTestProjectResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

