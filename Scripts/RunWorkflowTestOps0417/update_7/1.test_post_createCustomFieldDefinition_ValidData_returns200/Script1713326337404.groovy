import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new Team
team_request = new RequestObject()
team_request.setRestUrl("https://testops.katalon.io/api/v1/teams")
team_request.setRestRequestMethod("POST")
addAuthHeader(team_request)
addContentTypeHeader(team_request)

team_payload = '{"name": "Team1__unique__", "role": "OWNER", "users": [], "organizationId": 123}'
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))

team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)

// Step 2: Create a new Project
project_request = new RequestObject()
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)

project_payload = '{"name": "Project1__unique__", "teamId": ' + team_response.getResponseText('id') + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))

project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

// Step 3: Create a new Custom Field Definition
custom_field_request = new RequestObject()
custom_field_request.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions/create")
custom_field_request.setRestRequestMethod("POST")
addAuthHeader(custom_field_request)
addContentTypeHeader(custom_field_request)

custom_field_payload = '{"key": "CustomField1__unique__", "displayName": "Custom Field 1", "projectId": ' + project_response.getResponseText('id') + ', "organizationId": 123, "createdAt": "2022-01-01T00:00:00Z", "customFieldOptions": []}'
custom_field_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(custom_field_payload)))

custom_field_response = WSBuiltInKeywords.sendRequest(custom_field_request)
WSBuiltInKeywords.verifyResponseStatusCode(custom_field_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

