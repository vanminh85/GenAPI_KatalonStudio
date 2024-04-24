import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
	if (authToken) {
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new Project
projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectPayload = '{"name": "Project1__unique__", "teamId": 1}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 2: Create a new Run Configuration
runConfigRequest = new RequestObject()
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
runConfigPayload = '{"name": "RunConfig1__unique__", "command": "command", "projectId": 1, "teamId": 1, "testProjectId": 1}'
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

// Step 3: Create a new Scheduler with a non-existent Project
schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/9999/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
schedulerPayload = '{"name": "Scheduler1__unique__", "startTime": "2022-01-01T00:00:00Z", "interval": 1, "intervalUnit": "HOUR", "runConfigurationId": 1}'
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

