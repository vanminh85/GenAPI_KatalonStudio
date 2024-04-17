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
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = '{"name": "Team1__unique__", "role": "OWNER", "users": [], "organizationId": 1}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 2: Create a new Project
def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"name": "Project1__unique__", "teamId": ' + teamId + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 3: Create a CustomFieldDefinitionResource
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']
def customFieldRequest = new RequestObject()
customFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions/create")
customFieldRequest.setRestRequestMethod("POST")
addAuthHeader(customFieldRequest)
addContentTypeHeader(customFieldRequest)
def customFieldPayload = '{"key": "Key1__unique__", "displayName": "Display1__unique__", "projectId": ' + projectId + ', "organizationId": 1, "customFieldOptions": []}'
customFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(customFieldPayload)))
def customFieldResponse = WSBuiltInKeywords.sendRequest(customFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(customFieldResponse, 200)

// Step 4: Update the CustomFieldDefinitionResource
def customFieldId = new JsonSlurper().parseText(customFieldResponse.getResponseText())['id']
def updatedCustomFieldRequest = new RequestObject()
updatedCustomFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions")
updatedCustomFieldRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedCustomFieldRequest)
addContentTypeHeader(updatedCustomFieldRequest)
def updatedCustomFieldPayload = '{"id": ' + customFieldId + ', "key": "Key1__unique__", "displayName": "Display1__unique__", "projectId": ' + projectId + ', "organizationId": 1, "customFieldOptions": []}'
updatedCustomFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedCustomFieldPayload)))
def updatedCustomFieldResponse = WSBuiltInKeywords.sendRequest(updatedCustomFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedCustomFieldResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
