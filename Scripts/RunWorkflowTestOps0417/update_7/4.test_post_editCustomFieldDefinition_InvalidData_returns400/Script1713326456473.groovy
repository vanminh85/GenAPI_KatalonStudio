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

// Step 1: Create a new Team
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	name: "Team1__unique__",
	role: "OWNER",
	users: [],
	organizationId: 1
])))
teamRequest.setBodyContent(teamPayload)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 2: Create a new Project
def projectId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	name: "Project1__unique__",
	teamId: projectId,
	timezone: "UTC",
	status: "ACTIVE",
	canAutoIntegrate: true,
	sampleProject: false
])))
projectRequest.setBodyContent(projectPayload)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 3: Create a CustomFieldDefinitionResource
def customFieldRequest = new RequestObject()
def customFieldProjectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']
customFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions/create")
customFieldRequest.setRestRequestMethod("POST")
addAuthHeader(customFieldRequest)
addContentTypeHeader(customFieldRequest)
def customFieldPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	key: "Field1__unique__",
	displayName: "Field 1",
	projectId: customFieldProjectId,
	organizationId: 1,
	createdAt: "2022-01-01T00:00:00Z",
	customFieldOptions: []
])))
customFieldRequest.setBodyContent(customFieldPayload)
def customFieldResponse = WSBuiltInKeywords.sendRequest(customFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(customFieldResponse, 200)

// Step 4: Update CustomFieldDefinitionResource with invalid data
def invalidCustomFieldRequest = new RequestObject()
invalidCustomFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions")
invalidCustomFieldRequest.setRestRequestMethod("PUT")
addAuthHeader(invalidCustomFieldRequest)
addContentTypeHeader(invalidCustomFieldRequest)
def invalidCustomFieldPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	key: "Field1__unique__",
	displayName: "Field 1",
	projectId: customFieldProjectId,
	organizationId: 1,
	createdAt: "2022-01-01T00:00:00Z",
	customFieldOptions: []
])))
invalidCustomFieldRequest.setBodyContent(invalidCustomFieldPayload)
def invalidCustomFieldResponse = WSBuiltInKeywords.sendRequest(invalidCustomFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(invalidCustomFieldResponse, 400)

// Step 5: Verify response status code is 400
assert invalidCustomFieldResponse.getStatusCode() == 400

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
