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
def teamPayload = '{"name": "TeamName__unique__", "role": "OWNER", "users": [], "organizationId": 123}'
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
def projectPayload = '{"name": "ProjectName__unique__", "teamId": ' + teamId + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
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
def customFieldPayload = '{"key": "CustomFieldKey__unique__", "displayName": "CustomFieldDisplayName__unique__", "projectId": ' + projectId + ', "organizationId": 123, "createdAt": "2022-01-01T00:00:00Z", "customFieldOptions": []}'
customFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(customFieldPayload)))
def customFieldResponse = WSBuiltInKeywords.sendRequest(customFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(customFieldResponse, 200)

// Step 4: Delete the CustomFieldDefinitionResource
def customFieldId = new JsonSlurper().parseText(customFieldResponse.getResponseText())['id']
def deleteCustomFieldRequest = new RequestObject()
deleteCustomFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions")
deleteCustomFieldRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteCustomFieldRequest)
addContentTypeHeader(deleteCustomFieldRequest)
def deleteCustomFieldPayload = '{"id": ' + customFieldId + '}'
deleteCustomFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(deleteCustomFieldPayload)))
def deleteCustomFieldResponse = WSBuiltInKeywords.sendRequest(deleteCustomFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteCustomFieldResponse, 200)

// Step 5: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(deleteCustomFieldResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
