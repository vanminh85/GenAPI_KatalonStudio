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

def runConfigurationRequest = new RequestObject()
runConfigurationRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigurationRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigurationRequest)
addContentTypeHeader(runConfigurationRequest)
def runConfigurationPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	name: "Test Run Configuration_${uuid}",
	command: "echo 'Hello, World!'",
	projectId: 123,
	teamId: 456
])))
runConfigurationRequest.setBodyContent(runConfigurationPayload)
def runConfigurationResponse = WSBuiltInKeywords.sendRequest(runConfigurationRequest)
def runConfigurationId = new JsonSlurper().parseText(runConfigurationResponse.getResponseText())["id"]

def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/789/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	name: "Test Scheduler_${uuid}",
	startTime: "2022-01-01T00:00:00Z",
	runConfigurationId: runConfigurationId
])))
schedulerRequest.setBodyContent(schedulerPayload)
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)

WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}