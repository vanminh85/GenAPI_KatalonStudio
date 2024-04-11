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

// Step 1: Create a Test Project
def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)

def testProjectPayload = '{"name": "TestProject", "description": "Test Project Description", "defaultTestProject": true, "type": "GIT", "gitRepository": {"name": "TestProjectRepo"}}'
testProjectRequest.setBodyContent(new HttpTextBodyContent(testProjectPayload))

def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())['id']

// Step 2: Update the Test Project
def updateTestProjectRequest = new RequestObject()
updateTestProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${testProjectId}")
updateTestProjectRequest.setRestRequestMethod("PUT")
addAuthHeader(updateTestProjectRequest)
addContentTypeHeader(updateTestProjectRequest)

def updatedTestProjectPayload = '{"name": "UpdatedTestProject", "description": "Updated Test Project Description"}'
updateTestProjectRequest.setBodyContent(new HttpTextBodyContent(updatedTestProjectPayload))

def updateTestProjectResponse = WSBuiltInKeywords.sendRequest(updateTestProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateTestProjectResponse, 200)

