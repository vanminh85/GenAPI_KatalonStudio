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
def organizationPayload = '{"name": "Organization__unique__", "role": "OWNER", "quotaKSE": 100, "quotaEngine": 50, "usedKSE": 0, "usedEngine": 0, "accountId": 123, "accountUUID": "abc-123", "tier": "PROFESSIONAL", "requestedUserVerified": true}'
def organizationRequest = new RequestObject()
organizationRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(organizationPayload)))
organizationRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
organizationRequest.setRestRequestMethod("POST")
addAuthHeader(organizationRequest)
addContentTypeHeader(organizationRequest)
def organizationResponse = WSBuiltInKeywords.sendRequest(organizationRequest)
WSBuiltInKeywords.verifyResponseStatusCode(organizationResponse, 200)

def organizationId = new JsonSlurper().parseText(organizationResponse.getResponseText())['id']

// Step 2: Create a new Team
def teamPayload = '{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + organizationId + '}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']

// Step 3: Create a new Agent
def agentPayload = '{"name": "Agent__unique__", "ip": "192.168.1.1", "uuid": "123-456-789", "os": "Linux", "teamId": ' + teamId + ', "hostname": "hostname", "active": true, "threshold": 10, "numExecutingJobs": 0, "numAssignedJobs": 0, "agentVersion": "1.0", "deleted": false}'
def agentRequest = new RequestObject()
agentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
agentRequest.setRestUrl("https://testops.katalon.io/api/v1/agent")
agentRequest.setRestRequestMethod("POST")
addAuthHeader(agentRequest)
addContentTypeHeader(agentRequest)
def agentResponse = WSBuiltInKeywords.sendRequest(agentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(agentResponse, 200)

// Step 4: Send a POST request to /api/v1/agent/threshold with the created Agent data
def thresholdRequest = new RequestObject()
thresholdRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
thresholdRequest.setRestUrl("https://testops.katalon.io/api/v1/agent/threshold")
thresholdRequest.setRestRequestMethod("POST")
addAuthHeader(thresholdRequest)
addContentTypeHeader(thresholdRequest)
def thresholdResponse = WSBuiltInKeywords.sendRequest(thresholdRequest)
WSBuiltInKeywords.verifyResponseStatusCode(thresholdResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

