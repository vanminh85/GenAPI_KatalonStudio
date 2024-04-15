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

// Request 1: Create a RunConfigurationResource data
def runConfigData = '{"id": 123, "name": "Test Run Config", "command": "run_tests", "projectId": 456, "teamId": 789, "testProjectId": 101, "releaseId": 202, "testSuiteCollectionId": 303, "testSuiteId": 404, "executionProfileId": 505, "baselineCollectionGroupOrder": 1, "timeOut": 60, "kobitonDeviceId": "device123", "configType": "TSC", "cloudType": "LOCAL_AGENT", "executionMode": "SEQUENTIAL", "enabledKobitonIntegration": true, "enabledTestCloudTunnel": false, "triggerMode": "TESTOPS_SCHEDULER", "browserType": "CHROME", "xrayImportReportType": "PUSH_MANUALLY", "externalTestPlanId": "plan123"}'

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(runConfigData)))
request1.setRestUrl("https://testops.katalon.io/api/v1/run-configurations/123/link-release")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)

def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
