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

// Step 1: Create a new Organization resource
def orgRequest = new RequestObject()
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Organization__unique__", "role": "OWNER"}'))
orgRequest.setBodyContent(orgPayload)
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

// Step 2: Create a new Team resource
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + orgResponse.getResponseText()) + '}')
teamRequest.setBodyContent(teamPayload)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 3: Create a new Project resource
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Project__unique__", "teamId": ' + teamResponse.getResponseText()) + '}')
projectRequest.setBodyContent(projectPayload)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 4: Create a new RunConfiguration resource with an invalid teamId
def runConfigRequest = new RequestObject()
runConfigRequest.setRestUrl("https://testops.katalon.io/api/v1/run-configurations")
runConfigRequest.setRestRequestMethod("POST")
addAuthHeader(runConfigRequest)
addContentTypeHeader(runConfigRequest)
def runConfigPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "RunConfig__unique__", "command": "echo \'Running tests\'", "projectId": ' + projectResponse.getResponseText()) + ' + 1, "teamId": ' + teamResponse.getResponseText() + ' + 1}')
runConfigRequest.setBodyContent(runConfigPayload)
def runConfigResponse = WSBuiltInKeywords.sendRequest(runConfigRequest)
WSBuiltInKeywords.verifyResponseStatusCode(runConfigResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}