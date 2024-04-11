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

// Step 1
def orgTrialRequestPayload = '{"organization": {"id": 1}, "userRequest": {"id": 1}, "status": "PENDING", "updatedAt": "2022-01-01T00:00:00Z", "formRequest": "Sample form", "feature": "KSE"}'
def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgTrialRequestPayload)))
request1.setRestUrl("${GlobalVariable.base_url}/api/v1/organizations/1/trial-request?feature=KSE")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2
def teamPayload = '{"id": 1, "name": "Team__unique__", "role": "OWNER", "users": [], "organization": {"id": 1}, "organizationId": 1}'
def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
request2.setRestUrl("${GlobalVariable.base_url}/api/v1/teams")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3
def projectPayload = '{"id": 1, "name": "Project__unique__", "teamId": 1, "team": {"id": 1}, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
def request3 = new RequestObject()
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
request3.setRestUrl("${GlobalVariable.base_url}/api/v1/projects")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

// Step 4
def runConfigPayload = '{"id": 1, "name": "RunConfig__unique__", "command": "echo \'Hello, World!\'", "projectId": 1, "teamId": 1, "testProjectId": 1, "releaseId": 1, "testSuiteCollectionId": 1, "testSuiteId": 1, "executionProfileId": 1, "baselineCollectionGroupOrder": 1, "timeOut": 60, "kobitonDeviceId": "device123", "configType": "TSC"}'
def request4 = new RequestObject()
request4.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigPayload)))
request4.setRestUrl("${GlobalVariable.base_url}/api/v1/run-configurations")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
def response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 200)

// Step 5
def schedulerPayload = '{"id": 1, "name": "Scheduler__unique__", "startTime": "2022-01-01T00:00:00Z", "nextTime": "2022-01-01T01:00:00Z", "endTime": "2022-01-01T02:00:00Z", "active": true, "interval": 30, "intervalUnit": "MINUTE", "runConfigurationId": 1, "runConfiguration": {"id": 1}, "exceededLimitTime": false}'
def request5 = new RequestObject()
request5.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(schedulerPayload)))
request5.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/1/schedulers")
request5.setRestRequestMethod("POST")
addAuthHeader(request5)
addContentTypeHeader(request5)
def response5 = WSBuiltInKeywords.sendRequest(request5)
WSBuiltInKeywords.verifyResponseStatusCode(response5, 200)

// Step 6
def request6 = new RequestObject()
request6.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/1/schedulers/1")
request6.setRestRequestMethod("GET")
addAuthHeader(request6)
def response6 = WSBuiltInKeywords.sendRequest(request6)
WSBuiltInKeywords.verifyResponseStatusCode(response6, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
