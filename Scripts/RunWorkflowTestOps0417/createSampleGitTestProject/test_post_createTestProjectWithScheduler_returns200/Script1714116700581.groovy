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
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TeamName__unique__", "role": "OWNER", "users": [], "organizationId": 1}')))

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "ProjectName__unique__", "teamId": ' + teamId + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}')))

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']

def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample-git-test-project")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TestProjectName__unique__", "description": "Test project description", "defaultTestProject": true, "uploadFileId": 1, "projectId": ' + projectId + ', "teamId": ' + teamId + ', "type": "KS"}')))

def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText())['id']

def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + testProjectId + "/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "SchedulerName__unique__", "startTime": "2023-01-01T00:00:00Z", "nextTime": "2023-01-01T00:00:00Z", "endTime": "2023-01-01T00:00:00Z", "active": true, "interval": 1, "intervalUnit": "DAY", "runConfigurationId": 1, "exceededLimitTime": false}')))

def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

