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

// Step 1
team_payload = '{"name": "Test Team", "role": "OWNER", "organizationId": 123}'
team_request = new RequestObject()
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
team_request.setRestUrl("https://testops.katalon.io/api/v1/teams")
team_request.setRestRequestMethod("POST")
addAuthHeader(team_request)
addContentTypeHeader(team_request)
team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)
team_id = new JsonSlurper().parseText(team_response.getResponseText())['id']

// Step 2
agent_payload = '{"name": "Test Agent", "teamId": ' + team_id + ', "ip": "192.168.1.1"}'
agent_request = new RequestObject()
agent_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agent_payload)))
agent_request.setRestUrl("https://testops.katalon.io/api/v1/agent")
agent_request.setRestRequestMethod("POST")
addAuthHeader(agent_request)
addContentTypeHeader(agent_request)
agent_response = WSBuiltInKeywords.sendRequest(agent_request)
WSBuiltInKeywords.verifyResponseStatusCode(agent_response, 200)

// Step 3
circleci_agent_payload = '{"name": "Test CircleCI Agent", "url": "https://circleci.com", "username": "testuser", "token": "testtoken", "project": "testproject", "vcsType": "github", "branch": "main", "teamId": ' + team_id + ', "apiKey": "testapikey"}'
circleci_agent_request = new RequestObject()
circleci_agent_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(circleci_agent_payload)))
circleci_agent_request.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent")
circleci_agent_request.setRestRequestMethod("POST")
addAuthHeader(circleci_agent_request)
addContentTypeHeader(circleci_agent_request)
circleci_agent_response = WSBuiltInKeywords.sendRequest(circleci_agent_request)
WSBuiltInKeywords.verifyResponseStatusCode(circleci_agent_response, 200)
circleci_agent_id = new JsonSlurper().parseText(circleci_agent_response.getResponseText())['id']

// Step 4
circleci_agent_get_request = new RequestObject()
circleci_agent_get_request.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent/" + circleci_agent_id)
circleci_agent_get_request.setRestRequestMethod("GET")
addAuthHeader(circleci_agent_get_request)
circleci_agent_get_response = WSBuiltInKeywords.sendRequest(circleci_agent_get_request)
WSBuiltInKeywords.verifyResponseStatusCode(circleci_agent_get_response, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
