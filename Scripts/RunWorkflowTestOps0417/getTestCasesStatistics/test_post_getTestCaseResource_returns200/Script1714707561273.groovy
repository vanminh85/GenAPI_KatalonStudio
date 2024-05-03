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

// Step 1: Create a new Team
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "Test Team", "role": "OWNER"]))))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2: Create a new Project under the Team
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "Test Project", "teamId": teamId])))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)
def projectData = new JsonSlurper().parseText(projectResponse.getResponseText())

// Step 3: Create a new Test Project under the Project
def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/upload-script-repo")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "Test Test Project", "projectId": projectData["id"], "batch": "batch", "folderPath": "folderPath", "fileName": "fileName", "uploadedPath": "uploadedPath"])))
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)
def testProjectData = new JsonSlurper().parseText(testProjectResponse.getResponseText())

// Step 4: Create a new Test Case Resource
def testCaseRequest = new RequestObject()
testCaseRequest.setRestUrl("https://testops.katalon.io/api/v1/test-cases/update")
testCaseRequest.setRestRequestMethod("POST")
addAuthHeader(testCaseRequest)
addContentTypeHeader(testCaseRequest)
testCaseRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "Test Case", "path": "testPath", "previousStatus": "PASSED", "alias": "Test Alias", "testModuleId": 123, "webUrl": "http://test-url.com", "description": "Test Description", "project": projectData, "testProject": testProjectData, "type": "TEST_CASE", "testType": "G4_TEST_CASE", "urlId": "testId"])))
def testCaseResponse = WSBuiltInKeywords.sendRequest(testCaseRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testCaseResponse, 200)

// Step 5: Retrieve the Test Case Resource
def testCaseId = new JsonSlurper().parseText(testCaseResponse.getResponseText())["id"]
def getTestCaseRequest = new RequestObject()
getTestCaseRequest.setRestUrl("https://testops.katalon.io/api/v1/test-cases/${testCaseId}")
getTestCaseRequest.setRestRequestMethod("GET")
addAuthHeader(getTestCaseRequest)
def getTestCaseResponse = WSBuiltInKeywords.sendRequest(getTestCaseRequest)
WSBuiltInKeywords.verifyResponseStatusCode(getTestCaseResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

