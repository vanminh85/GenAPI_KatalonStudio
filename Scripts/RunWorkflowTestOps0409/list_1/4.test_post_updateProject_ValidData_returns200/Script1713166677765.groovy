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
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)

def teamPayload = '{"id": 1, "name": "Team1__unique__", "role": "OWNER", "users": [], "organization": {"id": 1}}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 3: Create a new Project
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)

def projectPayload = '{"id": 1, "name": "Project1__unique__", "teamId": ' + teamId + ', "team": {"id": ' + teamId + '}, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": true}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

// Step 5: Update the Project
def updatedProjectRequest = new RequestObject()
updatedProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
updatedProjectRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedProjectRequest)
addContentTypeHeader(updatedProjectRequest)

def updatedProjectPayload = '{"id": ' + projectId + ', "name": "UpdatedProject1__unique__", "teamId": ' + teamId + ', "team": {"id": ' + teamId + '}, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": true}'
updatedProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedProjectPayload)))

def updatedProjectResponse = WSBuiltInKeywords.sendRequest(updatedProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedProjectResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
