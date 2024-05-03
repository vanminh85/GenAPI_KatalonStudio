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

def projectPayload = '{"name": "ProjectName__unique__", "teamId": 123, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true}'

def createProjectRequest = new RequestObject()
createProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
createProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
createProjectRequest.setRestRequestMethod("POST")
addAuthHeader(createProjectRequest)
addContentTypeHeader(createProjectRequest)

def createProjectResponse = WSBuiltInKeywords.sendRequest(createProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createProjectResponse, 200)

def createdProjectId = new JsonSlurper().parseText(createProjectResponse.getResponseText()).id
println("Created project ID is ${createdProjectId}")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

