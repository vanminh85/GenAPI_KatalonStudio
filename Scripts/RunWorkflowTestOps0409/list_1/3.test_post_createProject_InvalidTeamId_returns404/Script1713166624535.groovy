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
    "name": "Project1",
    "teamId": -1,
    "team": {
        "id": -1,
        "name": "Team1__unique__",
        "role": "OWNER",
        "users": [],
        "organization": {
            "id": -1,
            "name": "Organization1__unique__",
            "role": "OWNER",
            "orgFeatureFlag": {},
            "quotaKSE": 100,
            "machineQuotaKSE": 10,
            "quotaUnlimitedKSE": 1000,
            "quotaEngine": 50,
            "machineQuotaEngine": 5,
            "quotaUnlimitedEngine": 500,
            "usedKSE": 50,
            "usedUnlimitedKSE": 500,
            "usedEngine": 25,
            "usedUnlimitedEngine": 250,
            "quotaTestOps": 100,
            "usedTestOps": 50,
            "numberUser": 10,
            "quotaFloatingEngine": 20,
            "usedFloatingEngine": 10,
            "canCreateOfflineKSE": true,
            "canCreateOfflineUnlimitedKSE": true,
            "canCreateOfflineRE": true,
            "canCreateOfflineUnlimitedEngine": true,
            "subscriptionExpiryDateEngine": "2023-01-01T00:00:00Z",
            "subscriptionExpiryDateUnlimitedEngine": "2023-01-01T00:00:00Z",
            "subscriptionExpiryDateKSE": "2023-01-01T00:00:00Z",
            "subscriptionExpiryDateUnlimitedKSE": "2023-01-01T00:00:00Z",
            "subscriptionExpiryDateFloatingEngine": "2023-01-01T00:00:00Z",
            "subscriptionExpiryDateTestOps": "2023-01-01T00:00:00Z",
            "subscribed": true,
            "ksePaygo": true,
            "krePaygo": true,
            "paygoQuota": 1000,
            "domain": "example.com",
            "subdomainUrl": "subdomain.example.com",
            "strictDomain": true,
            "logoUrl": "https://example.com/logo.png",
            "samlSSO": true,
            "kreLicense": true,
            "mostRecentProjectAccessedAt": "2022-01-01T00:00:00Z",
            "accountId": -1,
            "accountUUID": "123e4567-e89b-12d3-a456-426614174000",
            "testOpsFeature": "KSE",
            "platformFeature": "ENGINE",
            "tier": "PROFESSIONAL",
            "requestedUserVerified": true
        },
        "organizationId": -1
    },
    "timezone": "UTC",
    "status": "ACTIVE",
    "canAutoIntegrate": true,
    "sampleProject": true
}
'''

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))
request.setRestUrl("https://testops.katalon.io/api/v1/projects")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 404)

println("Step 2 - Response Status Code: " + response.getStatusCode())

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
