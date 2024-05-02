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
def request_org = new RequestObject()
request_org.setRestUrl("https://testops.katalon.io/api/v1/organizations")
request_org.setRestRequestMethod("POST")
addAuthHeader(request_org)
addContentTypeHeader(request_org)
def payload_org = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	name: "Organization__unique__",
	role: "OWNER",
	domain: "organization.com",
	subdomainUrl: "org",
	tier: "FREE",
	requestedUserVerified: true
])))
request_org.setBodyContent(payload_org)
def response_org = WSBuiltInKeywords.sendRequest(request_org)
WSBuiltInKeywords.verifyResponseStatusCode(response_org, 200)

// Step 2: Create a new Team under the Organization
def request_team = new RequestObject()
request_team.setRestUrl("https://testops.katalon.io/api/v1/teams")
request_team.setRestRequestMethod("POST")
addAuthHeader(request_team)
addContentTypeHeader(request_team)
def payload_team = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	name: "Team__unique__",
	role: "OWNER",
	organizationId: new JsonSlurper()).parseText(response_org.getResponseText())['id']
])))
request_team.setBodyContent(payload_team)
def response_team = WSBuiltInKeywords.sendRequest(request_team)
WSBuiltInKeywords.verifyResponseStatusCode(response_team, 200)

// Step 3: Create a new Project under the Team
def request_project = new RequestObject()
request_project.setRestUrl("https://testops.katalon.io/api/v1/projects")
request_project.setRestRequestMethod("POST")
addAuthHeader(request_project)
addContentTypeHeader(request_project)
def payload_project = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	name: "Project__unique__",
	teamId: new JsonSlurper()).parseText(response_team.getResponseText())['id'],
	timezone: "UTC",
	status: "ACTIVE",
	canAutoIntegrate: true,
	sampleProject: false
]))
request_project.setBodyContent(payload_project)
def response_project = WSBuiltInKeywords.sendRequest(request_project)
WSBuiltInKeywords.verifyResponseStatusCode(response_project, 200)

// Step 4: Create multiple Test Cases
def request_test_cases = new RequestObject()
request_test_cases.setRestUrl("https://testops.katalon.io/api/v1/test-cases/update")
request_test_cases.setRestRequestMethod("POST")
addAuthHeader(request_test_cases)
addContentTypeHeader(request_test_cases)
def payload_test_cases = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	[
		name: "Test Case 1__unique__",
		path: "/test-case-1",
		previousStatus: "PASSED",
		alias: "TC1",
		testModuleId: 1,
		webUrl: "https://test-case-1.com",
		description: "Test Case 1 Description",
		project: new JsonSlurper()).parseText(response_project.getResponseText())
	],
	[
		name: "Test Case 2__unique__",
		path: "/test-case-2",
		previousStatus: "FAILED",
		alias: "TC2",
		testModuleId: 2,
		webUrl: "https://test-case-2.com",
		description: "Test Case 2 Description",
		project: new JsonSlurper().parseText(response_project.getResponseText())
	]
]))
request_test_cases.setBodyContent(payload_test_cases)
def response_test_cases = WSBuiltInKeywords.sendRequest(request_test_cases)
WSBuiltInKeywords.verifyResponseStatusCode(response_test_cases, 200)

// Step 5: List all Test Cases
def request_list_test_cases = new RequestObject()
request_list_test_cases.setRestUrl("https://testops.katalon.io/api/v1/test-cases")
request_list_test_cases.setRestRequestMethod("GET")
addAuthHeader(request_list_test_cases)
def response_list_test_cases = WSBuiltInKeywords.sendRequest(request_list_test_cases)
WSBuiltInKeywords.verifyResponseStatusCode(response_list_test_cases, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}