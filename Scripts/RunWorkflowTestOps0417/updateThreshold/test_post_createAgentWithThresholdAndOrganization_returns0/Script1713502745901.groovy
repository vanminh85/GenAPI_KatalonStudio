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
def organizationPayload = '{"name": "Organization__unique__", "role": "OWNER", "quotaKSE": 100, "quotaEngine": 50, "usedKSE": 0, "usedEngine": 0, "accountId": 123, "accountUUID": "12345678-1234-5678-1234-567812345678", "tier": "FREE", "requestedUserVerified": true}'
def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(organizationPayload)))
request1.setRestUrl("${GlobalVariable.base_url}/api/v1/organizations/${id}/trial-request")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Create a new Team
def teamPayload = '{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + response1.getResponseText() + '.organization.id}'
def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
request2.setRestUrl("${GlobalVariable.base_url}/api/v1/teams")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3: Create a new Agent
def agentPayload = '{"name": "Agent__unique__", "ip": "192.168.1.1", "uuid": "12345678-1234-5678-1234-567812345679", "os": "Linux", "teamId": ' + response2.getResponseText() + '.id, "hostname": "hostname", "active": true, "threshold": 10, "numExecutingJobs": 0, "numAssignedJobs": 0, "agentVersion": "1.0", "deleted": false}'
def request3 = new RequestObject()
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
request3.setRestUrl("${GlobalVariable.base_url}/api/v1/agent")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

// Step 4: Send a POST request to /api/v1/organizations/{id}/trial-request
def request4 = new RequestObject()
request4.setRestUrl("${GlobalVariable.base_url}/api/v1/organizations/${id}/trial-request")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
def response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 200)

// Step 5: Send a POST request to /api/v1/agent/threshold with the created Agent data
def request5 = new RequestObject()
request5.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(agentPayload)))
request5.setRestUrl("${GlobalVariable.base_url}/api/v1/agent/threshold")
request5.setRestRequestMethod("PUT")
addAuthHeader(request5)
addContentTypeHeader(request5)
def response5 = WSBuiltInKeywords.sendRequest(request5)
WSBuiltInKeywords.verifyResponseStatusCode(response5, 200)

// Step 6: Verify that the response status code is 0
if (response5.getStatusCode() == 0) {
	println("Status code is 0")
} else {
	println("Status code is not 0")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
