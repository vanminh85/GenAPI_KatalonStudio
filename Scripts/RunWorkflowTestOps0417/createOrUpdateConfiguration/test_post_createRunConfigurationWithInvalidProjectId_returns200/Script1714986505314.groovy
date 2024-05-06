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

// Step 1: Create a new Organization resource
org_payload = '{"name": "Organization__unique__", "role": "OWNER"}'
org_request = new RequestObject()
org_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(org_payload)))
org_request.setRestUrl("https://testops.katalon.io/api/v1/organizations")
org_request.setRestRequestMethod("POST")
addAuthHeader(org_request)
addContentTypeHeader(org_request)
org_response = WSBuiltInKeywords.sendRequest(org_request)
WSBuiltInKeywords.verifyResponseStatusCode(org_response, 200)

// Step 2: Create a new Team resource
team_payload = '{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + org_response.getResponseText() + '.id}'
team_request = new RequestObject()
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
team_request.setRestUrl("https://testops.katalon.io/api/v1/teams")
team_request.setRestRequestMethod("POST")
addAuthHeader(team_request)
addContentTypeHeader(team_request)
team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)

// Step 3: Create a new Project resource
project_payload = '{"name": "Project__unique__", "teamId": ' + team_response.getResponseText() + '.id}'
project_request = new RequestObject()
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

// Step 4: Create a new RunConfiguration resource with an invalid projectId
run_config_payload = '{"name": "RunConfig__unique__", "command": "echo \'Running test\'", "projectId": 9999, "teamId": ' + team_response.getResponseText() + '.teamId}'
run_config_request = new RequestObject()
run_config_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(run_config_payload)))
run_config_request.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
run_config_request.setRestRequestMethod("POST")
addAuthHeader(run_config_request)
addContentTypeHeader(run_config_request)
run_config_response = WSBuiltInKeywords.sendRequest(run_config_request)
WSBuiltInKeywords.verifyResponseStatusCode(run_config_response, 200)

// Step 5: Verify that the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(run_config_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

