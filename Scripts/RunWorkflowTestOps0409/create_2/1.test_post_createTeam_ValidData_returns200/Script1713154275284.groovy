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

// Step 1: Create a new Organization
def orgRequest = new RequestObject()
def orgPayload = '{"organization": {"id": 1, "name": "Organization__unique__", "role": "OWNER", "orgFeatureFlag": {"organizationId": 1, "subDomain": false, "strictDomain": false, "sso": false, "whitelistIp": false, "circleCi": false, "testOpsIntegration": false}}, "userRequest": {"id": 1, "name": "User__unique__", "role": "OWNER"}, "status": "PENDING", "updatedAt": "2022-01-01T00:00:00Z", "formRequest": "Sample form request", "feature": "KSE"}'
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations/{id}/trial-request")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
def orgId = new JsonSlurper().parseText(orgResponse.getResponseText())["organization"]["id"]

// Step 3: Create a new Team
def teamRequest = new RequestObject()
def teamPayload = '{"id": 1, "name": "Team__unique__", "role": "OWNER", "users": [], "organization": {"id": ' + orgId + ', "name": "Organization__unique__", "role": "OWNER", "orgFeatureFlag": {"organizationId": ' + orgId + ', "subDomain": false, "strictDomain": false, "sso": false, "whitelistIp": false, "circleCi": false, "testOpsIntegration": false}}, "organizationId": ' + orgId + '}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
