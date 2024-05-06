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

// Step 1: Create a new Job
def jobRequest = new RequestObject()
jobRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/jobs/update-job")
jobRequest.setRestRequestMethod("POST")
addAuthHeader(jobRequest)
addContentTypeHeader(jobRequest)
def jobPayload = '{"buildNumber": 123, "status": "QUEUED", "queuedAt": "2022-01-01T10:00:00Z", "startTime": "2022-01-01T10:05:00Z", "stopTime": "2022-01-01T10:10:00Z", "runConfigurationId": 1, "triggerBy": "MANUAL", "duration": 300, "triggerAt": "2022-01-01T10:05:00Z", "processId": 456, "nodeStatus": "PENDING_CANCELED"}'
jobRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(jobPayload)))
def jobResponse = WSBuiltInKeywords.sendRequest(jobRequest)
WSBuiltInKeywords.verifyResponseStatusCode(jobResponse, 200)

// Step 2: Create a new Scheduler
def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/1/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerPayload = '{"name": "Scheduler__unique__", "startTime": "2022-01-01T10:00:00Z", "nextTime": "2022-01-01T11:00:00Z", "endTime": "2022-01-01T12:00:00Z", "active": true, "interval": 60, "intervalUnit": "MINUTE", "runConfigurationId": 1}'
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

// Step 3: Create a new Run Configuration
def runConfigRequest = new RequestObject()
runConfigRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
def runConfigPayload = '{"name": "RunConfig__unique__", "command": "echo \'Hello, World!\'", "projectId": 1, "teamId": 1, "testProjectId": 1, "releaseId": 1, "testSuiteCollectionId": 1, "testSuiteId": 1, "executionProfileId": 1, "baselineCollectionGroupOrder": 1, "timeOut": 600, "configType": "TSC", "cloudType": "K8S", "executionMode": "SEQUENTIAL", "triggerMode": "TESTOPS_SCHEDULER", "browserType": "CHROME"}'
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

// Step 4: Create a new Smart Scheduler Resource
def smartSchedulerRequest = new RequestObject()
smartSchedulerRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/smart-scheduler/schedule")
smartSchedulerRequest.setRestRequestMethod("POST")
addAuthHeader(smartSchedulerRequest)
addContentTypeHeader(smartSchedulerRequest)
def smartSchedulerPayload = '{"runConfiguration": {"$ref": "#/components/schemas/RunConfigurationResource"}, "scheduler": {"$ref": "#/components/schemas/SchedulerResource"}}'
smartSchedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(smartSchedulerPayload)))
def smartSchedulerResponse = WSBuiltInKeywords.sendRequest(smartSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(smartSchedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

