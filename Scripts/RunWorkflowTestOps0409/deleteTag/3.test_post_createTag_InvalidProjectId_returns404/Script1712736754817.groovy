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

// Step 1: Create a new OrganizationResource
org_payload = '{"name": "TestOrg", "role": "OWNER"}'
org_request = new RequestObject()
org_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(org_payload)))
org_request.setRestUrl("https://testops.katalon.io/api/v1/organizations")
org_request.setRestRequestMethod("POST")
addAuthHeader(org_request)
addContentTypeHeader(org_request)
org_response = WSBuiltInKeywords.sendRequest(org_request)
WSBuiltInKeywords.verifyResponseStatusCode(org_response, 200)
organizationId = new JsonSlurper().parseText(org_response.getResponseText())["id"]

// Step 2: Make a POST request to create a Tag with invalid projectId
tag_payload = '{"name": "TestTag", "projectId": 9999, "organizationId": $organizationId}'
tag_request = new RequestObject()
tag_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(tag_payload)))
tag_request.setRestUrl("https://testops.katalon.io/api/v1/tags")
tag_request.setRestRequestMethod("POST")
addAuthHeader(tag_request)
addContentTypeHeader(tag_request)
tag_response = WSBuiltInKeywords.sendRequest(tag_request)
WSBuiltInKeywords.verifyResponseStatusCode(tag_response, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}