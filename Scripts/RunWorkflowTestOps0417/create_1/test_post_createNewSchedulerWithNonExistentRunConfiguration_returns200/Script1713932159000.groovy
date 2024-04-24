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

def projectPayload = '{"name": "Project1__unique__", "teamId": 1}'
def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']

def runConfigPayload = '{"name": "RunConfig1__unique__", "command": "command1", "projectId": ' + projectId + ', "teamId": 1, "testProjectId": 1}'
def runConfigRequest = new RequestObject()
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

def schedulerPayload = '{"name": "Scheduler1__unique__", "startTime": "2022-01-01T00:00:00Z", "interval": 1, "intervalUnit": "HOUR", "runConfigurationId": 9999}'
def schedulerRequest = new RequestObject()
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + projectId + "/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

