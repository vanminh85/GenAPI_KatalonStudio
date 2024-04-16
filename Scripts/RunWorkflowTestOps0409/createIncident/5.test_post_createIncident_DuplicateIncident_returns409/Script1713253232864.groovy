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
def teamPayload = '{"name": "Test Team__unique__", "role": "OWNER", "users": [], "organizationId": 1}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']

// Step 3: Create a new Project
def projectPayload = '{"name": "Test Project__unique__", "teamId": ' + teamId + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']

// Step 5: Create a new Incident
def incidentPayload = '{"name": "Test Incident", "description": "Test Description", "projectId": ' + projectId + ', "teamId": ' + teamId + ', "urlIds": [], "executionTestResultIds": [], "order": 1}'
def incidentRequest = new RequestObject()
incidentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(incidentPayload)))
incidentRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/incidents")
incidentRequest.setRestRequestMethod("POST")
addAuthHeader(incidentRequest)
addContentTypeHeader(incidentRequest)
def incidentResponse = WSBuiltInKeywords.sendRequest(incidentRequest)
def statusCode = incidentResponse.getStatusCode()

// Step 6: Send the same request as step 5 again
def duplicateIncidentResponse = WSBuiltInKeywords.sendRequest(incidentRequest)
def statusCodeDuplicate = duplicateIncidentResponse.getStatusCode()

// Step 7: Verify the response status code is 409 Conflict
assert statusCodeDuplicate == 409

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}