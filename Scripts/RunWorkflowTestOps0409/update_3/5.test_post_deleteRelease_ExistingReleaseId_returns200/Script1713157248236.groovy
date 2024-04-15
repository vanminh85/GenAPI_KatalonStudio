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

def releaseData = '''
{
    "id": 123,
    "name": "Test Release__unique__",
    "startTime": "2022-01-01",
    "endTime": "2022-01-31",
    "description": "Test Release Description",
    "projectId": 456,
    "closed": false,
    "createdAt": "2022-01-01T00:00:00Z",
    "externalRelease": {"id": 789},
    "releaseStatistics": {
        "release": {"id": 123},
        "totalPassed": 100,
        "totalFailed": 10,
        "totalExecution": 110,
        "totalDefect": 5,
        "totalDuration": 3600
    },
    "releaseStatus": "READY"
}
'''

def createReleaseRequest = new RequestObject()
createReleaseRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(releaseData)))
createReleaseRequest.setRestUrl("https://testops.katalon.io/api/v1/releases")
createReleaseRequest.setRestRequestMethod("POST")
addAuthHeader(createReleaseRequest)
addContentTypeHeader(createReleaseRequest)

def createReleaseResponse = WSBuiltInKeywords.sendRequest(createReleaseRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createReleaseResponse, 200)

def releaseId = 123

def deleteReleaseRequest = new RequestObject()
deleteReleaseRequest.setRestUrl("https://testops.katalon.io/api/v1/releases/${releaseId}")
deleteReleaseRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteReleaseRequest)

def deleteReleaseResponse = WSBuiltInKeywords.sendRequest(deleteReleaseRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteReleaseResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
