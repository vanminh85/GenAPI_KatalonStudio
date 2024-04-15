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

def payload = '''
{
    "name": "Team1__unique__",
    "role": "INVALID_ROLE",
    "users": [],
    "organization": {
        "id": 1,
        "name": "Organization1__unique__",
        "role": "OWNER",
        "orgFeatureFlag": {
            "organizationId": 1,
            "subDomain": false,
            "strictDomain": true,
            "sso": false,
            "whitelistIp": true,
            "circleCi": false,
            "testOpsIntegration": true
        },
        "quotaKSE": 10,
        "machineQuotaKSE": 5,
        "quotaUnlimitedKSE": 20,
        "quotaEngine": 15,
        "machineQuotaEngine": 8,
        "quotaUnlimitedEngine": 25,
        "usedKSE": 2,
        "usedUnlimitedKSE": 5,
        "usedEngine": 8,
        "usedUnlimitedEngine": 10,
        "quotaTestOps": 5,
        "usedTestOps": 2,
        "numberUser": 10,
        "quotaFloatingEngine": 5,
        "usedFloatingEngine": 2,
        "canCreateOfflineKSE": true,
        "canCreateOfflineUnlimitedKSE": false,
        "canCreateOfflineRE": true,
        "canCreateOfflineUnlimitedEngine": false,
        "subscriptionExpiryDateEngine": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateUnlimitedEngine": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateKSE": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateUnlimitedKSE": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateFloatingEngine": "2022-12-31T23:59:59Z",
        "subscriptionExpiryDateTestOps": "2022-12-31T23:59:59Z",
        "subscribed": true,
        "ksePaygo": false,
        "krePaygo": true,
        "paygoQuota": 100,
        "domain": "example.com",
        "subdomainUrl": "subdomain.example.com",
        "strictDomain": false,
        "logoUrl": "https://example.com/logo.png",
        "samlSSO": true,
        "kreLicense": false,
        "mostRecentProjectAccessedAt": "2022-01-01T12:00:00Z",
        "accountId": 12345,
        "accountUUID": "123e4567-e89b-12d3-a456-426614174000",
        "testOpsFeature": "TESTOPS",
        "platformFeature": "ENGINE",
        "tier": "PROFESSIONAL",
        "requestedUserVerified": true
    },
    "organizationId": 1
}
'''

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))
request.setRestUrl("https://testops.katalon.io/api/v1/teams")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}