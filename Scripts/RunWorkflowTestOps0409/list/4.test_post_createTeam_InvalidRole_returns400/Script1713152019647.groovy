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

def orgFeatureFlagPayload = '{"organizationId": 123, "subDomain": true, "strictDomain": false, "sso": true, "whitelistIp": false, "circleCi": false, "testOpsIntegration": true}'
def orgResourcePayload = '{"id": 456, "name": "TestOrg", "role": "OWNER", "orgFeatureFlag": ' + orgFeatureFlagPayload + ', "quotaKSE": 100, "machineQuotaKSE": 50}'
def teamPayload = '{"id": 789, "name": "TestTeam", "role": "INVALID_ROLE", "users": [], "organization": {"id": 456, "name": "TestOrg", "role": "OWNER", "orgFeatureFlag": ' + orgFeatureFlagPayload + ', "organizationId": 456}}'

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgFeatureFlagPayload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/organization-feature-flags")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgResourcePayload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/organizations")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def request3 = new RequestObject()
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
request3.setRestUrl("https://testops.katalon.io/api/v1/teams")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
