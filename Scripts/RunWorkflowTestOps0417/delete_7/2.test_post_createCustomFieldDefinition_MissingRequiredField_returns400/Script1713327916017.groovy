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
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)

def projectPayload = '{"name": "Project__unique__", "teamId": 123, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 2: Create a new CustomFieldDefinitionResource with missing required field
def customFieldRequest = new RequestObject()
customFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions/create")
customFieldRequest.setRestRequestMethod("POST")
addAuthHeader(customFieldRequest)
addContentTypeHeader(customFieldRequest)

def customFieldPayload = '{"key": "CustomField__unique__", "displayName": "Custom Field", "projectId": 456, "organizationId": 789, "createdAt": "2022-01-01T00:00:00Z", "customFieldOptions": []}'
customFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(customFieldPayload)))

def customFieldResponse = WSBuiltInKeywords.sendRequest(customFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(customFieldResponse, 400)

// Step 3: Verify response status code is 400
assert customFieldResponse.getStatusCode() == 400

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
