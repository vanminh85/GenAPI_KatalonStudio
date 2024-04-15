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

// Step 1: Create a ReleaseResource data for an existing release
release_data = '{"id": 123, "name": "Test Release", "startTime": "2022-01-01", "endTime": "2022-01-31", "description": "Test Release Description", "projectId": 456, "closed": false, "createdAt": "2022-01-01T00:00:00Z", "externalRelease": {"id": 789, "name": "External Release"}, "releaseStatistics": {"release": {"startTime": "2022-01-01", "endTime": "2022-01-31", "projectId": 456, "externalRelease": {"id": 789, "name": "External Release"}}, "totalPassed": 100, "totalFailed": 10, "totalExecution": 110, "totalDefect": 5, "totalDuration": 3600}, "builds": [], "releaseStatus": "READY"}'

request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(release_data)))
request1.setRestUrl("${GlobalVariable.base_url}/api/v1/releases")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)

response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Send a DELETE request to /api/v1/releases/{id} with the ID of the existing release
release_id = 123

request2 = new RequestObject()
request2.setRestUrl("${GlobalVariable.base_url}/api/v1/releases/${release_id}")
request2.setRestRequestMethod("DELETE")
addAuthHeader(request2)

response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
