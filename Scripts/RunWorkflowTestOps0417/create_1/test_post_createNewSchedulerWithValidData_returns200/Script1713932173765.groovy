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
projectPayload = '{"name": "Project1__unique__", "teamId": 1}'
projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Extract projectId from the response
projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']

// Step 2: Create a new Run Configuration
runConfigPayload = '{"name": "RunConfig1__unique__", "command": "command1", "projectId": ' + projectId + ', "teamId": 1, "testProjectId": 1}'
runConfigRequest = new RequestObject()
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

// Extract runConfigurationId from the response
runConfigId = new JsonSlurper().parseText(runConfigResponse.getResponseText())['id']

// Step 3: Create a new Scheduler
schedulerPayload = '{"name": "Scheduler1__unique__", "startTime": "2022-01-01T00:00:00Z", "interval": 1, "intervalUnit": "DAY", "runConfigurationId": ' + runConfigId + '}'
schedulerRequest = new RequestObject()
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + projectId + "/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}


