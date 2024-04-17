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

def teamPayload = '{"name": "Team__unique__", "role": "OWNER", "users": [], "organizationId": 1}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

def circleciAgentPayload = '{"name": "CircleCIAgent__unique__", "url": "https://circleci.com", "username": "username__unique__", "token": "token__unique__", "project": "project__unique__", "vcsType": "git", "branch": "main", "teamId": teamId, "apiKey": "apiKey__unique__"}'
def circleciAgentRequest = new RequestObject()
circleciAgentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(circleciAgentPayload)))
circleciAgentRequest.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent")
circleciAgentRequest.setRestRequestMethod("POST")
addAuthHeader(circleciAgentRequest)
addContentTypeHeader(circleciAgentRequest)
def circleciAgentResponse = WSBuiltInKeywords.sendRequest(circleciAgentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(circleciAgentResponse, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
