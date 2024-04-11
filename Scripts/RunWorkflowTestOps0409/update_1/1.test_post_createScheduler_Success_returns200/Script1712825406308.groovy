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

// Step 1: Create a new Run Configuration
def runConfigRequest = new RequestObject()
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    "name": "TestRunConfig_${uuid}",
    "command": "test_command",
    "projectId": 1,
    "teamId": 1
]))))

def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
def runConfigId = (new JsonSlurper().parseText(runConfigResponse.getResponseText()))["id"]

// Step 2: Extract the created Run Configuration's ID
runConfigId = (new JsonSlurper().parseText(runConfigResponse.getResponseText()))["id"]

// Step 3: Create a new Project
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    "name": "TestProject_${uuid}",
    "teamId": 1
]))))

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
def projectId = (new JsonSlurper().parseText(projectResponse.getResponseText()))["id"]

// Step 4: Extract the created Project's ID
projectId = (new JsonSlurper().parseText(projectResponse.getResponseText()))["id"]

// Step 5: Create a new Team
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    "name": "TestTeam_${uuid}"
]))))

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = (new JsonSlurper().parseText(teamResponse.getResponseText()))["id"]

// Step 6: Extract the created Team's ID
teamId = (new JsonSlurper().parseText(teamResponse.getResponseText()))["id"]

// Step 7: Create a new Scheduler
def schedulerRequest = new RequestObject()
schedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${projectId}/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    "name": "TestScheduler_${uuid}",
    "startTime": "2022-01-01T00:00:00Z",
    "interval": 1,
    "intervalUnit": "DAY",
    "runConfigurationId": runConfigId
]))))

def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
def statusCode = schedulerResponse.getStatusCode()

// Step 8: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
