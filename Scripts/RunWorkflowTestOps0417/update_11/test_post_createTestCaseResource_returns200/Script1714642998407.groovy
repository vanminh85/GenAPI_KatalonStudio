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
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new Organization
requestOrg = new RequestObject()
requestOrg.setRestUrl("https://testops.katalon.io/api/v1/organizations")
requestOrg.setRestRequestMethod("POST")
addAuthHeader(requestOrg)
addContentTypeHeader(requestOrg)
payloadOrg = '{"name": "Organization__unique__", "role": "OWNER", "domain": "organization.com", "subdomainUrl": "org", "tier": "FREE", "requestedUserVerified": true}'
requestOrg.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payloadOrg)))
responseOrg = WSBuiltInKeywords.sendRequest(requestOrg)
WSBuiltInKeywords.verifyResponseStatusCode(responseOrg, 200)

// Step 2: Create a new Team under the Organization
requestTeam = new RequestObject()
requestTeam.setRestUrl("https://testops.katalon.io/api/v1/teams")
requestTeam.setRestRequestMethod("POST")
addAuthHeader(requestTeam)
addContentTypeHeader(requestTeam)
payloadTeam = '{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + responseOrg.getResponseText() + '.id}'
requestTeam.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payloadTeam)))
responseTeam = WSBuiltInKeywords.sendRequest(requestTeam)
WSBuiltInKeywords.verifyResponseStatusCode(responseTeam, 200)

// Step 3: Create a new Project under the Team
requestProject = new RequestObject()
requestProject.setRestUrl("https://testops.katalon.io/api/v1/projects")
requestProject.setRestRequestMethod("POST")
addAuthHeader(requestProject)
addContentTypeHeader(requestProject)
payloadProject = '{"name": "Project__unique__", "teamId": ' + responseTeam.getResponseText() + '.id, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
requestProject.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payloadProject)))
responseProject = WSBuiltInKeywords.sendRequest(requestProject)
WSBuiltInKeywords.verifyResponseStatusCode(responseProject, 200)

// Step 4: Create a new Test Case
requestTestCase = new RequestObject()
requestTestCase.setRestUrl("https://testops.katalon.io/api/v1/test-cases/update")
requestTestCase.setRestRequestMethod("POST")
addAuthHeader(requestTestCase)
addContentTypeHeader(requestTestCase)
payloadTestCase = '{"name": "Test Case__unique__", "path": "/testcases", "previousStatus": "PASSED", "alias": "TC", "testModuleId": 1, "webUrl": "https://example.com", "description": "This is a test case", "project": ' + responseProject.getResponseText() + '}'
requestTestCase.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payloadTestCase)))
responseTestCase = WSBuiltInKeywords.sendRequest(requestTestCase)
WSBuiltInKeywords.verifyResponseStatusCode(responseTestCase, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

