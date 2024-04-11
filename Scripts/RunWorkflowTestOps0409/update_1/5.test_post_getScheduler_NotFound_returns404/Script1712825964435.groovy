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

def runConfigRequest = new RequestObject()
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
def runConfigPayload = JsonOutput.toJson([
	"name": "TestRunConfig_${uuid}",
	"command": "test_command",
	"projectId": 1,
	"teamId": 1
])
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
def runConfigId = new JsonSlurper().parseText(runConfigResponse.getResponseText())["id"]

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = JsonOutput.toJson([
	"name": "TestProject_${uuid}",
	"teamId": 1
])
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = JsonOutput.toJson([
	"name": "TestTeam_${uuid}"
])
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${projectId}/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerPayload = JsonOutput.toJson([
	"name": "TestScheduler_${uuid}",
	"startTime": "2022-01-01T00:00:00Z",
	"interval": 60,
	"intervalUnit": "MINUTE",
	"runConfigurationId": runConfigId
])
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
def schedulerId = new JsonSlurper().parseText(schedulerResponse.getResponseText())["id"]

def nonExistentSchedulerId = 9999
def nonExistentSchedulerRequest = new RequestObject()
nonExistentSchedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${projectId}/schedulers/${nonExistentSchedulerId}")
nonExistentSchedulerRequest.setRestRequestMethod("GET")
addAuthHeader(nonExistentSchedulerRequest)
addContentTypeHeader(nonExistentSchedulerRequest)
def nonExistentSchedulerResponse = WSBuiltInKeywords.sendRequest(nonExistentSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(nonExistentSchedulerResponse, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
