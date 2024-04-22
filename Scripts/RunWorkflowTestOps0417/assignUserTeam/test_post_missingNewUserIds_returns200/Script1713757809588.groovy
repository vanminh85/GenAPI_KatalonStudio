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

def teamPayload = '{"name": "Test Team__unique__", "role": "OWNER", "users": [], "organization": null, "organizationId": 0}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
def teamId = (new JsonSlurper().parseText(teamResponse.getResponseText()))["id"]

def usersParams = ["teamId": teamId]
def usersRequest = new RequestObject()
usersRequest.setRestUrl("https://testops.katalon.io/api/v1/users/add")
usersRequest.setRestRequestMethod("POST")
usersRequest.setRestParameters(usersParams as List<TestObjectProperty>)
addAuthHeader(usersRequest)
def usersResponse = WSBuiltInKeywords.sendRequest(usersRequest)
def statusCode = usersResponse.getStatusCode()

WSBuiltInKeywords.verifyResponseStatusCode(usersResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
