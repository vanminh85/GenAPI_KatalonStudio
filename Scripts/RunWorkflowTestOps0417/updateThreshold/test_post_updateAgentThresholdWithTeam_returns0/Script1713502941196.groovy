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

def orgPayload = '{"name": "Organization__unique__", "role": "OWNER", "quotaKSE": 100, "quotaEngine": 50, "usedKSE": 0, "usedEngine": 0, "accountId": 123, "accountUUID": "abc123", "tier": "FREE", "requestedUserVerified": true}'
def orgRequest = new RequestObject()
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
def orgId = (new JsonSlurper().parseText(orgResponse.getResponseText()))["id"]

def teamPayload = '{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + orgId + '}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = (new JsonSlurper().parseText(teamResponse.getResponseText()))["id"]

def agentPayload = '{"name": "Agent__unique__", "ip": "192.168.1.1", "uuid": "123456", "os": "Linux", "teamId": ' + teamId + ', "hostname": "hostname", "active": true, "threshold": 10, "numExecutingJobs": 0, "numAssignedJobs": 0, "agentVersion": "1.0", "deleted": false}'
def agentRequest = new RequestObject()
agentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
agentRequest.setRestUrl("https://testops.katalon.io/api/v1/agent")
agentRequest.setRestRequestMethod("POST")
addAuthHeader(agentRequest)
addContentTypeHeader(agentRequest)
def agentResponse = WSBuiltInKeywords.sendRequest(agentRequest)
def agentId = (new JsonSlurper().parseText(agentResponse.getResponseText()))["id"]

def updateThresholdPayload = '{"threshold": 20}'
def updateThresholdRequest = new RequestObject()
updateThresholdRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateThresholdPayload)))
updateThresholdRequest.setRestUrl("https://testops.katalon.io/api/v1/agent/threshold")
updateThresholdRequest.setRestRequestMethod("PUT")
addAuthHeader(updateThresholdRequest)
addContentTypeHeader(updateThresholdRequest)
def updateThresholdResponse = WSBuiltInKeywords.sendRequest(updateThresholdRequest)
def statusCode = updateThresholdResponse.getStatusCode()

if (statusCode == 0) {
	println("Test Passed")
} else {
	println("Test Failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

