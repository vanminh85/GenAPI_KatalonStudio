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

// Step 1: Create a new Scheduler
def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("${base_url}/api/v1/test-projects/${projectId}/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerPayload = JsonOutput.toJson(scheduler_payload)
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

// Step 2: Create a new Run Configuration
def runConfigRequest = new RequestObject()
runConfigRequest.setRestUrl("${base_url}/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
def runConfigPayload = JsonOutput.toJson(run_configuration_payload)
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

// Step 3: Create a new Smart Scheduler Resource
def smartSchedulerRequest = new RequestObject()
smartSchedulerRequest.setRestUrl("${base_url}/api/v1/smart-scheduler/schedule")
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

