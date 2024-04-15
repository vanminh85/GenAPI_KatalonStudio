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

// Request 1: Create a ReleaseResource data for an existing release
def releaseData = '{"id": 123, "name": "Test Release__unique__", "startTime": "2022-01-01", "endTime": "2022-01-31", "description": "Test Release Description", "projectId": 456, "closed": false, "createdAt": "2022-01-01T00:00:00Z", "externalRelease": {"id": 789, "name": "External Release__unique__"}, "releaseStatistics": {"release": {"startTime": "2022-01-01", "endTime": "2022-01-31", "projectId": 456, "externalRelease": {"id": 789, "name": "External Release__unique__"}}, "totalPassed": 100, "totalFailed": 10, "totalExecution": 110, "totalDefect": 5, "totalDuration": 3600}, "builds": [], "releaseStatus": "READY"}'

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(releaseData)))
request1.setRestUrl("https://testops.katalon.io/api/v1/releases")
request1.setRestRequestMethod("PUT")
addAuthHeader(request1)
addContentTypeHeader(request1)

def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
