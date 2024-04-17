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

// Step 1: Create a new Project
def projectPayload = '{"name": "Project__unique__", "teamId": 123, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']

// Step 2: Create a new CustomFieldDefinitionResource
def customFieldPayload = '{"key": "Key__unique__", "displayName": "Display Name", "projectId": ' + projectId + ', "organizationId": 456, "customFieldOptions": []}'
def customFieldRequest = new RequestObject()
customFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(customFieldPayload)))
customFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions/create")
customFieldRequest.setRestRequestMethod("POST")
addAuthHeader(customFieldRequest)
addContentTypeHeader(customFieldRequest)
def customFieldResponse = WSBuiltInKeywords.sendRequest(customFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(customFieldResponse, 200)

def customFieldId = new JsonSlurper().parseText(customFieldResponse.getResponseText())['id']

// Step 3: Delete the CustomFieldDefinitionResource
def deleteCustomFieldRequest = new RequestObject()
deleteCustomFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"id": ' + customFieldId + '}')))
deleteCustomFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions")
deleteCustomFieldRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteCustomFieldRequest)
addContentTypeHeader(deleteCustomFieldRequest)
def deleteCustomFieldResponse = WSBuiltInKeywords.sendRequest(deleteCustomFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteCustomFieldResponse, 200)

// Step 4: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(deleteCustomFieldResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
