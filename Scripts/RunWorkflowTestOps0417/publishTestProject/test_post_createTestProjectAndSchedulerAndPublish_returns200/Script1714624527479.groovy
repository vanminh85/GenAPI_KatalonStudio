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
def teamPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Team1__unique__"}'))
teamRequest.setBodyContent(teamPayload)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 2: Create a new Project under the Team
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Project1__unique__", "teamId": ' + teamId + '}'))
projectRequest.setBodyContent(projectPayload)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 3: Create a new Test Project under the Project
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']
def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TestProject1__unique__", "type": "KS", "projectId": ' + projectId + '}'))
testProjectRequest.setBodyContent(testProjectPayload)
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

// Step 4: Create a new Scheduler for the Test Project
def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())['id']
def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + testProjectId + "/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Scheduler1__unique__", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-02T00:00:00Z", "active": true, "interval": 1, "intervalUnit": "DAY", "runConfigurationId": 1, "exceededLimitTime": false}'))
schedulerRequest.setBodyContent(schedulerPayload)
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

// Step 5: Publish the Test Project
def publishRequest = new RequestObject()
publishRequest.setRestUrl("https://testops.katalon.io/api/v1/test-management/test-projects/" + testProjectId + "/publish")
publishRequest.setRestRequestMethod("POST")
addAuthHeader(publishRequest)
addContentTypeHeader(publishRequest)
def publishPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"message": "Publishing Test Project", "testCases": [], "testSuites": []}'))
publishRequest.setBodyContent(publishPayload)
def publishResponse = WSBuiltInKeywords.sendRequest(publishRequest)
WSBuiltInKeywords.verifyResponseStatusCode(publishResponse, 200)

// Step 6: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(publishResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

