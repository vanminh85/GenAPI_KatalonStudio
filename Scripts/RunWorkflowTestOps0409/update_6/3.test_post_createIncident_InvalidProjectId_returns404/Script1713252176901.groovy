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

def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)

def teamPayload = '{"id": 1, "name": "Team1__unique__", "role": "OWNER", "users": [], "organizationId": 1}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def incidentRequest = new RequestObject()
incidentRequest.setRestUrl("https://testops.katalon.io/api/v1/incidents")
incidentRequest.setRestRequestMethod("POST")
addAuthHeader(incidentRequest)
addContentTypeHeader(incidentRequest)

def incidentPayload = '{"id": 1, "name": "Incident1__unique__", "description": "Test Incident", "projectId": 9999, "teamId": 1, "urlIds": [], "executionTestResultIds": [], "order": 1, "createdAt": "2022-01-01T00:00:00", "updatedAt": "2022-01-01T00:00:00"}'
incidentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(incidentPayload)))

def incidentResponse = WSBuiltInKeywords.sendRequest(incidentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(incidentResponse, 404)
WSBuiltInKeywords.verifyResponseStatusCode(incidentResponse, 404)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
