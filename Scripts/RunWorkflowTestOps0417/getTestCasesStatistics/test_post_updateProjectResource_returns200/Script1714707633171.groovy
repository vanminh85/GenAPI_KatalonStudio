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

def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Test Team", "role": "OWNER"}')))

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Test Project", "teamId": ' + teamId + '}')))

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']

def updatedProjectRequest = new RequestObject()
updatedProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
updatedProjectRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedProjectRequest)
addContentTypeHeader(updatedProjectRequest)
updatedProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"id": ' + projectId + ', "name": "Updated Project", "teamId": ' + teamId + ', "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}')))

def updatedProjectResponse = WSBuiltInKeywords.sendRequest(updatedProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedProjectResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

