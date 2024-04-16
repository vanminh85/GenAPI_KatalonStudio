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

// Step 1: Create a new Team
def teamPayload = '{"name": "Test Team", "role": "OWNER", "users": [], "organizationId": 1}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']

// Step 3: Send a POST request to create an Incident
def incidentPayload = '{"name": "Test Incident", "description": "Test Description", "teamId": ' + teamId + ', "urlIds": [], "executionTestResultIds": [], "order": 1}'
def incidentRequest = new RequestObject()
incidentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(incidentPayload)))
incidentRequest.setRestUrl("https://testops.katalon.io/api/v1/incidents")
incidentRequest.setRestRequestMethod("POST")
addAuthHeader(incidentRequest)
addContentTypeHeader(incidentRequest)
def incidentResponse = WSBuiltInKeywords.sendRequest(incidentRequest)
def statusCode = incidentResponse.getStatusCode()

WSBuiltInKeywords.verifyResponseStatusCode(incidentResponse, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
