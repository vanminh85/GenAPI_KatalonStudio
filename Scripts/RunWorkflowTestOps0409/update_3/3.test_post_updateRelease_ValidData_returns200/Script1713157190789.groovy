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
    "id": 1,
    "name": "Test Release__unique__",
    "startTime": "2022-01-01",
    "endTime": "2022-01-31",
    "description": "Test Release Description",
    "projectId": 123,
    "closed": false,
    "createdAt": "2022-01-01T00:00:00Z",
    "externalRelease": {"id": 1, "name": "External Release__unique__"},
    "releaseStatistics": {
        "release": {"id": 1},
        "totalPassed": 10,
        "totalFailed": 5,
        "totalExecution": 15,
        "totalDefect": 2,
        "totalDuration": 3600
    },
    "releaseStatus": "READY"
}
'''

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(releaseData)))
request.setRestUrl("https://testops.katalon.io/api/v1/releases")
request.setRestRequestMethod("PUT")

addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
