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

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "Project_${uuid}", "teamId": 1])))
projectRequest.setBodyContent(projectPayload)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def runConfigRequest = new RequestObject()
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
def runConfigPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "RunConfig_${uuid}", "command": "Command_${uuid}", "projectId": 1, "teamId": 1, "testProjectId": 1])))
runConfigRequest.setBodyContent(runConfigPayload)
def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/1/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "Scheduler_${uuid}", "startTime": "2022-01-01T00:00:00Z", "interval": 5, "intervalUnit": "MINUTE", "runConfigurationId": 1])))
schedulerRequest.setBodyContent(schedulerPayload)
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

