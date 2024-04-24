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
testProjectPayload = '''
{
    "name": "TestProject__unique__",
    "description": "Test project description",
    "defaultTestProject": true,
    "uploadFileId": 1,
    "projectId": 1,
    "teamId": 1,
    "createdAt": "2022-01-01T00:00:00Z",
    "latestJob": {"id": 1, "name": "Latest Job"},
    "uploadFileName": "test_file",
    "type": "KS",
    "gitRepository": {"id": 1, "testProjectId": 1, "name": "GitRepo__unique__", "repository": "https://github.com/example.git", "branch": "main", "username": "user", "password": "pass", "accessKeyId": "access_key", "secretAccessKey": "secret_key", "projectId": 1, "teamId": 1, "createdAt": "2022-01-01T00:00:00Z", "updatedAt": "2022-01-01T00:00:00Z", "description": "Git repository description", "vcsType": "GITHUB", "shouldMergeTestResultsForNewScriptRepo": true},
    "testSuiteCollections": [],
    "dirty": false,
    "autonomous": true
}
'''

request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
request1.setRestUrl("${base_url}/api/v1/test-projects")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Update the Test Project script repository
updateScriptRepoPayload = '''
{
    "batch": "batch_script",
    "folderPath": "scripts",
    "fileName": "test_script.py",
    "uploadedPath": "uploaded/scripts/test_script.py"
}
'''

request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateScriptRepoPayload)))
request2.setRestUrl("${base_url}/api/v1/test-projects/1/update-script-repo")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
