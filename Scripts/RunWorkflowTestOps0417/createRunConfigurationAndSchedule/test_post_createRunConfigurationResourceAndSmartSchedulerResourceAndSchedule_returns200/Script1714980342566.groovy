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

// Request 1: Create a new Run Configuration
def runConfigurationRequest = new RequestObject()
runConfigurationRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	name: "TestRunConfig__unique__",
	command: "echo 'Hello, World!'",
	projectId: 123,
	teamId: 456,
	testProjectId: 789,
	releaseId: 1011,
	testSuiteCollectionId: 1213,
	testSuiteId: 1415,
	executionProfileId: 1617,
	baselineCollectionGroupOrder: 1819,
	timeOut: 20,
	configType: "TSC",
	cloudType: "K8S",
	executionMode: "SEQUENTIAL",
	triggerMode: "TESTOPS_SCHEDULER",
	browserType: "CHROME"
]))))
runConfigurationRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigurationRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigurationRequest)
addContentTypeHeader(runConfigurationRequest)

def runConfigurationResponse = WSBuiltInKeywords.sendRequest(runConfigurationRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigurationResponse, 200)

// Request 2: Create a new Smart Scheduler Resource
def smartSchedulerRequest = new RequestObject()
smartSchedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	runConfiguration: [
		name: "TestRunConfig__unique__"
	],
	scheduler: [
		id: 123,
		name: "TestScheduler__unique__",
		startTime: "2022-01-01T00:00:00Z",
		nextTime: "2022-01-01T01:00:00Z",
		endTime: "2022-01-01T02:00:00Z",
		active: true,
		interval: 60,
		intervalUnit: "MINUTE",
		runConfigurationId: 123,
		runConfiguration: [
			name: "TestRunConfig__unique__"
		],
		exceededLimitTime: false
	]
]))))
smartSchedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/smart-scheduler/schedule")
smartSchedulerRequest.setRestRequestMethod("POST")
addAuthHeader(smartSchedulerRequest)
addContentTypeHeader(smartSchedulerRequest)

def smartSchedulerResponse = WSBuiltInKeywords.sendRequest(smartSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(smartSchedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

