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
        auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
        request.getHttpHeaderProperties().add(auth_header)
    }
}

def addContentTypeHeader(request) {
    content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
    request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create necessary data for the dependent resources
projectId = 1__unique__
externalRelease = "external_release__unique__"
releaseStatistics = ["totalPassed": 10, "totalFailed": 5, "totalExecution": 15, "totalDefect": 2, "totalDuration": "2h"]
releaseStatus = "READY"

// Step 2: Send a POST request to /api/v1/releases with missing a required field in ReleaseResource
request = new RequestObject()
request.setRestUrl("https://testops.katalon.io/api/v1/releases")
request.setRestRequestMethod("POST")

payload = [
    "projectId": projectId,
    "externalRelease": externalRelease,
    "releaseStatistics": releaseStatistics,
    // Missing a required field here
    "releaseStatus": releaseStatus
]

request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(payload))))

addAuthHeader(request)
addContentTypeHeader(request)

response = WSBuiltInKeywords.sendRequest(request)

// Step 3: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(response, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
