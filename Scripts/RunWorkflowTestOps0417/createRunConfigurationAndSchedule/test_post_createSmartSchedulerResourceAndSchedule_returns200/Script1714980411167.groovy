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

// Step 1: Create a new Run Configuration
def runConfigurationRequest = new RequestObject()
runConfigurationRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigurationRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigurationRequest)
addContentTypeHeader(runConfigurationRequest)
def runConfigurationPayload = JsonOutput.toJson(run_configuration_payload)
runConfigurationRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigurationPayload)))
def runConfigurationResponse = WSBuiltInKeywords.sendRequest(runConfigurationRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigurationResponse, 200)

// Step 2: Create a new Scheduler
def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/123/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerPayload = JsonOutput.toJson(scheduler_payload)
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

// Step 3: Create a new Job
def jobRequest = new RequestObject()
jobRequest.setRestUrl("https://testops.katalon.io/api/v1/jobs/update-job")
jobRequest.setRestRequestMethod("POST")
addAuthHeader(jobRequest)
addContentTypeHeader(jobRequest)
def jobPayload = JsonOutput.toJson(job_payload)
jobRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(jobPayload)))
def jobResponse = WSBuiltInKeywords.sendRequest(jobRequest)
WSBuiltInKeywords.verifyResponseStatusCode(jobResponse, 200)

// Step 4: Create a new Smart Scheduler Resource
def smartSchedulerRequest = new RequestObject()
smartSchedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/smart-scheduler/schedule")
smartSchedulerRequest.setRestRequestMethod("POST")
addAuthHeader(smartSchedulerRequest)
addContentTypeHeader(smartSchedulerRequest)
def smartSchedulerPayload = JsonOutput.toJson(smart_scheduler_payload)
smartSchedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(smartSchedulerPayload)))
def smartSchedulerResponse = WSBuiltInKeywords.sendRequest(smartSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(smartSchedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

