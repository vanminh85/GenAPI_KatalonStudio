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

// Step 1: Create a new OrganizationTrialRequestResource
def orgId = 123
def feature = "KSE"
def orgTrialRequestPayload = [
    organization: [id: orgId],
    userRequest: [id: 1],
    status: "PENDING",
    updatedAt: "2022-01-01T00:00:00Z",
    formRequest: "Sample form request",
    feature: feature
]

def orgTrialRequestRequest = new RequestObject()
orgTrialRequestRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(orgTrialRequestPayload))))
orgTrialRequestRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/organizations/${orgId}/trial-request?feature=${feature}")
orgTrialRequestRequest.setRestRequestMethod("POST")
addAuthHeader(orgTrialRequestRequest)
addContentTypeHeader(orgTrialRequestRequest)

def orgTrialRequestResponse = WSBuiltInKeywords.sendRequest(orgTrialRequestRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgTrialRequestResponse, 200)

// Step 2: Create a new TeamResource
def teamPayload = [
    id: 1,
    name: "Team__unique__",
    role: "OWNER",
    users: [],
    organization: [id: orgId],
    organizationId: orgId
]

def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(teamPayload))))
teamRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 3: Create a new ProjectResource
def projectPayload = [
    id: 1,
    name: "Project__unique__",
    teamId: 1,
    team: [id: 1],
    timezone: "UTC",
    status: "ACTIVE",
    canAutoIntegrate: true,
    sampleProject: false
]

def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(projectPayload))))
projectRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 4: Create a new RunConfigurationResource
def runConfigPayload = [
    id: 1,
    name: "RunConfig__unique__",
    command: "echo 'Hello, World!'",
    projectId: 1,
    teamId: 1,
    testProjectId: 1,
    releaseId: 1,
    testSuiteCollectionId: 1,
    testSuiteId: 1,
    executionProfileId: 1,
    baselineCollectionGroupOrder: 1,
    timeOut: 60,
    configType: "TSC"
]

def runConfigRequest = new RequestObject()
runConfigRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(runConfigPayload))))
runConfigRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)

def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

// Step 5: Create a new SchedulerResource
def schedulerPayload = [
    id: 1,
    name: "Scheduler__unique__",
    startTime: "2022-01-01T00:00:00Z",
    nextTime: "2022-01-01T00:00:00Z",
    endTime: "2022-01-01T00:00:00Z",
    active: true,
    interval: 5,
    intervalUnit: "MINUTE",
    runConfigurationId: 1,
    runConfiguration: [id: 1],
    exceededLimitTime: false
]

def schedulerRequest = new RequestObject()
schedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(schedulerPayload))))
schedulerRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/1/schedulers")
schedulerRequest.setRestRequestMethod("POST")
addAuthHeader(schedulerRequest)
addContentTypeHeader(schedulerRequest)

def schedulerResponse = WSBuiltInKeywords.sendRequest(schedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(schedulerResponse, 200)

// Step 6: Delete the SchedulerResource
def deleteSchedulerRequest = new RequestObject()
deleteSchedulerRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/1/schedulers/1")
deleteSchedulerRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteSchedulerRequest)

def deleteSchedulerResponse = WSBuiltInKeywords.sendRequest(deleteSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteSchedulerResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
