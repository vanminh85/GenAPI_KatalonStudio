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

def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = '{"name": "TestProject__unique__", "description": "TestProjectDescription__unique__", "defaultTestProject": true, "uploadFileId": 1, "projectId": 1, "teamId": 1, "createdAt": "2022-01-01T00:00:00Z", "latestJob": {"id": 1}, "uploadFileName": "TestFile__unique__", "type": "KS", "gitRepository": {"id": 1}, "testSuiteCollections": [{"id": 1}], "dirty": false, "autonomous": true}'
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

def testFolderRequest = new RequestObject()
testFolderRequest.setRestUrl("https://testops.katalon.io/api/v1/test-folder/sub-folders-test-cases")
testFolderRequest.setRestRequestMethod("POST")
addAuthHeader(testFolderRequest)
addContentTypeHeader(testFolderRequest)
def testFolderPayload = '{"name": "TestFolder__unique__", "rawPath": "TestPath__unique__", "testProject": {"id": 1}, "treePath": "TreePath__unique__", "parentId": 1, "testCases": [{"id": 1}]}'
testFolderRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testFolderPayload)))
def testFolderResponse = WSBuiltInKeywords.sendRequest(testFolderRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testFolderResponse, 200)

def testReportsRequest = new RequestObject()
testReportsRequest.setRestUrl("https://testops.katalon.io/api/v1/testng/test-reports")
testReportsRequest.setRestRequestMethod("POST")
addAuthHeader(testReportsRequest)
addContentTypeHeader(testReportsRequest)
def testReportsPayload = '{"name": "TestFolder__unique__", "rawPath": "TestPath__unique__", "testProject": {"id": 1}, "treePath": "TreePath__unique__", "parentId": 1, "testCases": [{"id": 1}]}'
testReportsRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testReportsPayload)))
def testReportsResponse = WSBuiltInKeywords.sendRequest(testReportsRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testReportsResponse, 204)

if (testReportsResponse.getStatusCode() == 204) {
    println("Step 4 - Response status code is 204 - Test Passed")
} else {
    println("Step 4 - Response status code is not 204 - Test Failed")
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
