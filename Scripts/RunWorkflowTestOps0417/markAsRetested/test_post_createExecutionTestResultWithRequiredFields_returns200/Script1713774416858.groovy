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
def teamPayload = '{"name": "Team1__unique__", "role": "OWNER", "organizationId": 1}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"name": "Project1__unique__", "teamId": 1, "timezone": "UTC", "status": "ACTIVE"}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def executionRequest = new RequestObject()
executionRequest.setRestUrl("https://testops.katalon.io/api/v1/executions/1/rerun")
executionRequest.setRestRequestMethod("POST")
addAuthHeader(executionRequest)
def executionResponse = WSBuiltInKeywords.sendRequest(executionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionResponse, 200)

def testResultRequest = new RequestObject()
testResultRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/1/mask-as-retested")
testResultRequest.setRestRequestMethod("POST")
addAuthHeader(testResultRequest)
addContentTypeHeader(testResultRequest)
def testResultPayload = '{"status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "duration": 3600, "elapsedDuration": 3600, "totalTests": 10, "totalPassedTests": 10, "totalFailedTests": 0, "totalErrorTests": 0, "totalIncompleteTests": 0, "totalSkippedTests": 0, "totalDiffTests": 0, "totalDiffPassedTests": 0, "totalDiffFailedTests": 0, "totalDiffErrorTests": 0, "totalDiffIncompleteTests": 0, "executionId": 1, "executionOrder": 1, "testCaseId": 1, "projectId": 1}'
testResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testResultPayload)))
def testResultResponse = WSBuiltInKeywords.sendRequest(testResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testResultResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

