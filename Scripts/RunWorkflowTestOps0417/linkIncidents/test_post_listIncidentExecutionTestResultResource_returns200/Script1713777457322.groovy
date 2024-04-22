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
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	id: 1,
	name: "Team1__unique__",
	role: "OWNER",
	organizationId: 1
]))))

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	id: 1,
	name: "Project1__unique__",
	teamId: 1,
	timezone: "UTC",
	status: "ACTIVE",
	canAutoIntegrate: true,
	sampleProject: false
]))))

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def executionRequest = new RequestObject()
executionRequest.setRestUrl("https://testops.katalon.io/api/v1/executions/1/link-release")
executionRequest.setRestRequestMethod("POST")
addAuthHeader(executionRequest)
addContentTypeHeader(executionRequest)
executionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	id: 1,
	projectId: 1,
	releaseId: 1
]))))

def executionResponse = WSBuiltInKeywords.sendRequest(executionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionResponse, 200)

def executionTestResultRequest = new RequestObject()
executionTestResultRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/1__unique__/incidents")
executionTestResultRequest.setRestRequestMethod("POST")
addAuthHeader(executionTestResultRequest)
addContentTypeHeader(executionTestResultRequest)
executionTestResultRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	id: "1__unique__",
	testCase: [:],
	execution: [:],
	platform: [:],
	status: "PASSED",
	startTime: "2022-01-01T00:00:00Z",
	endTime: "2022-01-01T01:00:00Z",
	duration: 3600,
	testSuite: [:],
	executionTestSuite: [:],
	incidents: []
]))))

def executionTestResultResponse = WSBuiltInKeywords.sendRequest(executionTestResultRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionTestResultResponse, 200)

def getExecutionTestResultsRequest = new RequestObject()
getExecutionTestResultsRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/1__unique__/incidents")
getExecutionTestResultsRequest.setRestRequestMethod("GET")
addAuthHeader(getExecutionTestResultsRequest)

def getExecutionTestResultsResponse = WSBuiltInKeywords.sendRequest(getExecutionTestResultsRequest)
WSBuiltInKeywords.verifyResponseStatusCode(getExecutionTestResultsResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

