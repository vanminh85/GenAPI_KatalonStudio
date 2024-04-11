import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
    authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
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

// Step 1: Create a new Run Configuration
runConfigPayload = '{"name": "TestRunConfig_' + uuid + '", "command": "TestCommand_' + uuid + '", "projectId": 1, "teamId": 1}'
runConfigRequest = new RequestObject()
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
runConfigId = (new JsonSlurper().parseText(runConfigResponse.getResponseText()))["id"]

// Step 3: Create a new Project
projectPayload = '{"name": "TestProject_' + uuid + '", "teamId": 1}'
projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
projectId = (new JsonSlurper().parseText(projectResponse.getResponseText()))["id"]

// Step 5: Create a new Team
teamPayload = '{"name": "TestTeam_' + uuid + '"}'
teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
teamId = (new JsonSlurper().parseText(teamResponse.getResponseText()))["id"]

// Step 7: Create a new Scheduler with missing required fields
schedulerPayload = '{}'
schedulerRequest = new RequestObject()
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + projectId + "/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)

// Step 8: Verify the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
