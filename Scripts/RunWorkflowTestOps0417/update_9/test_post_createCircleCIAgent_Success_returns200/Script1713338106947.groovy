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

def teamPayload = '{"name": "Test Team", "role": "OWNER", "organizationId": 123}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']

def agentPayload = '{"name": "Test Agent", "teamId": ' + teamId + ', "ip": "192.168.1.1"}'
def agentRequest = new RequestObject()
agentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
agentRequest.setRestUrl("https://testops.katalon.io/api/v1/agent")
agentRequest.setRestRequestMethod("POST")
addAuthHeader(agentRequest)
addContentTypeHeader(agentRequest)
def agentResponse = WSBuiltInKeywords.sendRequest(agentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(agentResponse, 200)

def circleciAgentPayload = '{"name": "Test CircleCI Agent", "url": "https://circleci.com", "username": "testuser", "token": "testtoken", "project": "testproject", "vcsType": "github", "branch": "main", "teamId": ' + teamId + ', "apiKey": "testapikey"}'
def circleciAgentRequest = new RequestObject()
circleciAgentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(circleciAgentPayload)))
circleciAgentRequest.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent")
circleciAgentRequest.setRestRequestMethod("POST")
addAuthHeader(circleciAgentRequest)
addContentTypeHeader(circleciAgentRequest)
def circleciAgentResponse = WSBuiltInKeywords.sendRequest(circleciAgentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(circleciAgentResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

