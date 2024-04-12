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

// Step 1: Create a new Organization
org_request = new RequestObject()
org_request.setRestUrl("https://testops.katalon.io/api/v1/organizations")
org_request.setRestRequestMethod("POST")
addAuthHeader(org_request)
addContentTypeHeader(org_request)
org_payload = '{"id": 1, "name": "Organization1__unique__", "role": "OWNER"}'
org_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(org_payload)))
org_response = WSBuiltInKeywords.sendRequest(org_request)
WSBuiltInKeywords.verifyResponseStatusCode(org_response, 200)

// Step 2: Create a new Project
project_request = new RequestObject()
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_payload = '{"id": 1, "name": "Project1__unique__", "teamId": 1, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

// Step 3: Create a Tag
tag_request = new RequestObject()
tag_request.setRestUrl("https://testops.katalon.io/api/v1/tags")
tag_request.setRestRequestMethod("POST")
addAuthHeader(tag_request)
addContentTypeHeader(tag_request)
tag_payload = '{"id": 1, "name": "Tag1__unique__", "projectId": 1, "organizationId": 1}'
tag_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(tag_payload)))
tag_response = WSBuiltInKeywords.sendRequest(tag_request)
WSBuiltInKeywords.verifyResponseStatusCode(tag_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
