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

def teamPayload = '''
{
    "name": "TeamName__unique__",
    "role": "OWNER",
    "organization": {
        "id": 1,
        "name": "OrganizationName__unique__",
        "role": "OWNER",
        "orgFeatureFlag": {
            "organizationId": 1,
            "subDomain": false,
            "strictDomain": false,
            "sso": false,
            "whitelistIp": false,
            "circleCi": false,
            "testOpsIntegration": false
        },
        "quotaKSE": 0,
        "machineQuotaKSE": 0,
        "quotaUnlimitedKSE": 0,
        "quotaEngine": 0,
        "machineQuotaEngine": 0,
        "quotaUnlimitedEngine": 0,
        "usedKSE": 0,
        "usedUnlimitedKSE": 0,
        "usedEngine": 0,
        "usedUnlimitedEngine": 0,
        "quotaTestOps": 0,
        "usedTestOps": 0,
        "numberUser": 0,
        "quotaFloatingEngine": 0,
        "usedFloatingEngine": 0,
        "canCreateOfflineKSE": false,
        "canCreateOfflineUnlimitedKSE": false,
        "canCreateOfflineRE": false,
        "canCreateOfflineUnlimitedEngine": false,
        "subscriptionExpiryDateEngine": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateUnlimitedEngine": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateKSE": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateUnlimitedKSE": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateFloatingEngine": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateTestOps": "2022-12-31T23:59:59Z",
        "subscribed": false,
        "ksePaygo": false,
        "krePaygo": false,
        "paygoQuota": 0,
        "domain": "example.com",
        "subdomainUrl": "subdomain.example.com",
        "strictDomain": false,
        "logoUrl": "https://example.com/logo.png",
        "samlSSO": false,
        "kreLicense": false,
        "mostRecentProjectAccessedAt": "2022-01-01T00:00:00Z",
        "accountId": 1,
        "accountUUID": "123e4567-e89b-12d3-a456-426614174000",
        "testOpsFeature": "KSE",
        "platformFeature": "KSE",
        "tier": "FREE",
        "requestedUserVerified": false
    },
    "organizationId": 1
}
'''

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
request.setRestUrl("https://testops.katalon.io/api/v1/teams")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 404)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
