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

def orgTrialRequestPayload = '{"organization": {"id": 1}, "userRequest": {"id": 1}, "status": "PENDING", "updatedAt": "2022-01-01T00:00:00Z", "formRequest": "Sample form", "feature": "KSE"}'
def orgTrialRequestRequest = new RequestObject()
orgTrialRequestRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgTrialRequestPayload)))
orgTrialRequestRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations/1/trial-request?feature=KSE")
orgTrialRequestRequest.setRestRequestMethod("POST")
addAuthHeader(orgTrialRequestRequest)
addContentTypeHeader(orgTrialRequestRequest)
def orgTrialRequestResponse = WSBuiltInKeywords.sendRequest(orgTrialRequestRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgTrialRequestResponse, 200)

// Repeat the above pattern for the remaining steps with appropriate payloads and URLs

def teamPayload = '{"id": 1, "name": "Team__unique__", "role": "OWNER", "users": [], "organization": {"id": 1}, "organizationId": 1}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Repeat the pattern for the remaining steps

def projectPayload = '{"id": 1, "name": "Project__unique__", "teamId": 1, "team": {"id": 1}, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Repeat the pattern for the remaining steps

def runConfigPayload = '{"id": 1, "name": "RunConfig__unique__", "command": "echo \'Hello, World!\'", "projectId": 1, "teamId": 1, "testProjectId": 1, "releaseId": 1, "testSuiteCollectionId": 1, "testSuiteId": 1, "executionProfileId": 1, "baselineCollectionGroupOrder": 1, "timeOut": 60, "kobitonDeviceId": "device123", "configType": "TSC"}'
def runConfigRequest = new RequestObject()
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

// Repeat the pattern for the remaining steps

def schedulerPayload = '{"id": 1, "name": "Scheduler__unique__", "startTime": "2022-01-01T00:00:00Z", "nextTime": "2022-01-01T01:00:00Z", "endTime": "2022-01-01T02:00:00Z", "active": true, "interval": 60, "intervalUnit": "MINUTE", "runConfigurationId": 1, "runConfiguration": {"id": 1}, "exceededLimitTime": false}'
def schedulerRequest = new RequestObject()
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/1/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

// Repeat the pattern for the remaining steps

def invalidSchedulerPayload = '{"id": 1, "name": "UpdatedScheduler__unique__", "startTime": "2022-01-01T00:00:00Z", "nextTime": "2022-01-01T01:00:00Z", "endTime": "2022-01-01T02:00:00Z", "active": true, "interval": 60, "intervalUnit": "MINUTE", "runConfigurationId": 1, "runConfiguration": {"id": 1}, "exceededLimitTime": false, "invalidField": "InvalidValue"}'
def invalidSchedulerRequest = new RequestObject()
invalidSchedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(invalidSchedulerPayload)))
invalidSchedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/1/schedulers/1")
invalidSchedulerRequest.setRestRequestMethod("PUT")
addAuthHeader(invalidSchedulerRequest)
addContentTypeHeader(invalidSchedulerRequest)
def invalidSchedulerResponse = WSBuiltInKeywords.sendRequest(invalidSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(invalidSchedulerResponse, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
