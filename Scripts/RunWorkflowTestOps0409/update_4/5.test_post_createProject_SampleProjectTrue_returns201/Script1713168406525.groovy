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

// Step 1: Create a new Team
teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)

team_data = '{"name": "Test Team", "role": "OWNER", "users": [], "organizationId": 123}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_data)))

teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

if teamResponse.getStatusCode() == 200 {
    team_id = new JsonSlurper().parseText(teamResponse.getResponseText())['id']
    
    // Step 2: Create a new Project
    projectRequest = new RequestObject()
    projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
    projectRequest.setRestRequestMethod("POST")
    addAuthHeader(projectRequest)
    addContentTypeHeader(projectRequest)

    project_data = '{"name": "Test Project", "teamId": ' + team_id + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": true}'
    projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_data)))

    projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
    WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
