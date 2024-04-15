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

// Step 1: Create a new OrganizationFeatureFlagResource
def orgFeatureFlagRequest = new RequestObject()
def orgFeatureFlagPayload = '{"organizationId": 123, "subDomain": true, "strictDomain": false, "sso": true, "whitelistIp": false, "circleCi": false, "testOpsIntegration": true}'
orgFeatureFlagRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgFeatureFlagPayload)))
orgFeatureFlagRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/organization-feature-flags")
orgFeatureFlagRequest.setRestRequestMethod("POST")
addAuthHeader(orgFeatureFlagRequest)
addContentTypeHeader(orgFeatureFlagRequest)
def orgFeatureFlagResponse = WSBuiltInKeywords.sendRequest(orgFeatureFlagRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgFeatureFlagResponse, 200)

// Step 2: Create a new OrganizationResource
def orgResourceRequest = new RequestObject()
def orgResourcePayload = '{"id": 456, "name": "TestOrg", "role": "OWNER", "orgFeatureFlag": ' + orgFeatureFlagPayload + ', "quotaKSE": 100, "machineQuotaKSE": 50}'
orgResourceRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgResourcePayload)))
orgResourceRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/organizations")
orgResourceRequest.setRestRequestMethod("POST")
addAuthHeader(orgResourceRequest)
addContentTypeHeader(orgResourceRequest)
def orgResourceResponse = WSBuiltInKeywords.sendRequest(orgResourceRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResourceResponse, 200)

// Step 3: Send a POST request to create a new TeamResource
def teamRequest = new RequestObject()
def teamPayload = '{"id": 789, "name": "TestTeam", "role": "OWNER", "users": [], "organization": {"id": 456, "name": "TestOrg", "role": "OWNER", "orgFeatureFlag": ' + orgFeatureFlagPayload + ', "organizationId": 456}}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
