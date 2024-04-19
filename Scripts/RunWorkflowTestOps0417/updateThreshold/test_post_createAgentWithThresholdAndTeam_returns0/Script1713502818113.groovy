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

def orgPayload = '{"name": "OrgName__unique__", "role": "OWNER", "quotaKSE": 100, "quotaEngine": 50, "usedKSE": 10, "usedEngine": 5, "accountId": 123, "accountUUID": "abc123", "tier": "PROFESSIONAL", "requestedUserVerified": true}'
def orgRequest = new RequestObject()
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

def orgId = new JsonSlurper().parseText(orgResponse.getResponseText())['id']

def teamPayload = '{"name": "TeamName__unique__", "role": "OWNER", "organizationId": ' + orgId + '}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']

def agentPayload = '{"name": "AgentName__unique__", "ip": "192.168.1.1", "uuid": "agent123", "os": "Linux", "teamId": ' + teamId + ', "hostname": "hostname", "active": true, "threshold": 5, "numExecutingJobs": 2, "numAssignedJobs": 3, "agentVersion": "1.0", "deleted": false}'
def agentRequest = new RequestObject()
agentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
agentRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
agentRequest.setRestRequestMethod("POST")
addAuthHeader(agentRequest)
addContentTypeHeader(agentRequest)
def agentResponse = WSBuiltInKeywords.sendRequest(agentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(agentResponse, 200)

def thresholdPayload = '{"threshold": 10}'
def thresholdRequest = new RequestObject()
thresholdRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(thresholdPayload)))
thresholdRequest.setRestUrl("https://testops.katalon.io/api/v1/agent/threshold")
thresholdRequest.setRestRequestMethod("PUT")
addAuthHeader(thresholdRequest)
addContentTypeHeader(thresholdRequest)
def thresholdResponse = WSBuiltInKeywords.sendRequest(thresholdRequest)
WSBuiltInKeywords.verifyResponseStatusCode(thresholdResponse, 200)

assert thresholdResponse.getStatusCode() == 0

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

