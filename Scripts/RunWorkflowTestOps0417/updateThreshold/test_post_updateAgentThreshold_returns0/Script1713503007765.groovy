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

// Step 1: Create a new Organization
def orgPayload = '{"name": "OrgName__unique__", "role": "OWNER", "quotaKSE": 100, "quotaEngine": 50, "usedKSE": 0, "usedEngine": 0, "accountId": 123, "accountUUID": "abc-123", "tier": "FREE", "requestedUserVerified": true}'
def orgRequest = new RequestObject()
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

// Step 2: Create a new Team
def teamPayload = '{"name": "TeamName__unique__", "role": "OWNER", "organizationId": ' + orgResponse.getResponseText() + '.id}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 3: Create a new Agent
def agentPayload = '{"name": "AgentName__unique__", "ip": "192.168.1.1", "uuid": "123-456-789", "os": "Linux", "teamId": ' + teamResponse.getResponseText() + '.id, "hostname": "AgentHost__unique__", "active": true, "threshold": 10, "numExecutingJobs": 0, "numAssignedJobs": 0, "agentVersion": "1.0", "deleted": false}'
def agentRequest = new RequestObject()
agentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
agentRequest.setRestUrl("https://testops.katalon.io/api/v1/agent")
agentRequest.setRestRequestMethod("POST")
addAuthHeader(agentRequest)
addContentTypeHeader(agentRequest)
def agentResponse = WSBuiltInKeywords.sendRequest(agentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(agentResponse, 200)

// Step 4: Send a PUT request to /api/v1/agent/threshold with the created Agent data
def updateThresholdRequest = new RequestObject()
updateThresholdRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
updateThresholdRequest.setRestUrl("https://testops.katalon.io/api/v1/agent/threshold")
updateThresholdRequest.setRestRequestMethod("PUT")
addAuthHeader(updateThresholdRequest)
addContentTypeHeader(updateThresholdRequest)
def updateThresholdResponse = WSBuiltInKeywords.sendRequest(updateThresholdRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateThresholdResponse, 200)

// Step 5: Verify that the response status code is 0
if (updateThresholdResponse.getStatusCode() == 0) {
	println("Step 5 - Verify Status Code: Passed")
} else {
	println("Step 5 - Verify Status Code: Failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
