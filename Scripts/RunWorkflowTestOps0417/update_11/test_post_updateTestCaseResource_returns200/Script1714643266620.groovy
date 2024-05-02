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
request_org = new RequestObject()
request_org.setRestUrl("https://testops.katalon.io/api/v1/organizations")
request_org.setRestRequestMethod("POST")
addAuthHeader(request_org)
addContentTypeHeader(request_org)
payload_org = '{"name": "Organization__unique__", "role": "OWNER", "domain": "organization.com", "subdomainUrl": "org", "tier": "FREE", "requestedUserVerified": true}'
request_org.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_org)))
response_org = WSBuiltInKeywords.sendRequest(request_org)
WSBuiltInKeywords.verifyResponseStatusCode(response_org, 200)

// Step 2: Create a new Team under the Organization
request_team = new RequestObject()
request_team.setRestUrl("https://testops.katalon.io/api/v1/teams")
request_team.setRestRequestMethod("POST")
addAuthHeader(request_team)
addContentTypeHeader(request_team)
payload_team = '{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + response_org.getResponseText() + '}'
request_team.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_team)))
response_team = WSBuiltInKeywords.sendRequest(request_team)
WSBuiltInKeywords.verifyResponseStatusCode(response_team, 200)

// Step 3: Create a new Project under the Team
request_project = new RequestObject()
request_project.setRestUrl("https://testops.katalon.io/api/v1/projects")
request_project.setRestRequestMethod("POST")
addAuthHeader(request_project)
addContentTypeHeader(request_project)
payload_project = '{"name": "Project__unique__", "teamId": ' + response_team.getResponseText() + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
request_project.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_project)))
response_project = WSBuiltInKeywords.sendRequest(request_project)
WSBuiltInKeywords.verifyResponseStatusCode(response_project, 200)

// Step 4: Create a new Test Case
request_test_case = new RequestObject()
request_test_case.setRestUrl("https://testops.katalon.io/api/v1/test-cases/update")
request_test_case.setRestRequestMethod("POST")
addAuthHeader(request_test_case)
addContentTypeHeader(request_test_case)
payload_test_case = '{"name": "Test Case__unique__", "path": "/testcases", "previousStatus": "PASSED", "alias": "TC", "testModuleId": 1, "webUrl": "https://example.com", "description": "This is a test case", "project": ' + response_project.getResponseText() + '}'
request_test_case.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_test_case)))
response_test_case = WSBuiltInKeywords.sendRequest(request_test_case)
WSBuiltInKeywords.verifyResponseStatusCode(response_test_case, 200)

// Step 5: Update the created Test Case with new data
new_payload_test_case = '{"name": "Updated Test Case__unique__", "path": "/updated-testcases", "previousStatus": "FAILED", "alias": "UTC", "testModuleId": 2, "webUrl": "https://updated-example.com", "description": "This is an updated test case", "project": ' + response_project.getResponseText() + '}'
request_new_test_case = new RequestObject()
request_new_test_case.setRestUrl("https://testops.katalon.io/api/v1/test-cases/update")
request_new_test_case.setRestRequestMethod("POST")
addAuthHeader(request_new_test_case)
addContentTypeHeader(request_new_test_case)
request_new_test_case.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(new_payload_test_case)))
response_new_test_case = WSBuiltInKeywords.sendRequest(request_new_test_case)
WSBuiltInKeywords.verifyResponseStatusCode(response_new_test_case, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

