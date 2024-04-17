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

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"name": "Project__unique__", "teamId": 1, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

def releaseRequest = new RequestObject()
releaseRequest.setRestUrl("https://testops.katalon.io/api/v1/releases")
releaseRequest.setRestRequestMethod("POST")
addAuthHeader(releaseRequest)
addContentTypeHeader(releaseRequest)
def releasePayload = '{"name": "Release__unique__", "startTime": "2022-01-01", "endTime": "2022-01-02", "description": "Test Release", "projectId": ' + projectId + ', "closed": false, "createdAt": "2022-01-01T00:00:00Z", "releaseStatus": "READY"}'
releaseRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(releasePayload)))
def releaseResponse = WSBuiltInKeywords.sendRequest(releaseRequest)
def releaseId = new JsonSlurper().parseText(releaseResponse.getResponseText())["id"]

def buildRequest = new RequestObject()
buildRequest.setRestUrl("https://testops.katalon.io/api/v1/build")
buildRequest.setRestRequestMethod("POST")
addAuthHeader(buildRequest)
addContentTypeHeader(buildRequest)
def buildPayload = '{"projectId": ' + projectId + ', "releaseId": ' + releaseId + ', "name": "Build__unique__", "description": "Test Build", "date": "2022-01-01T00:00:00Z"}'
buildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(buildPayload)))
def buildResponse = WSBuiltInKeywords.sendRequest(buildRequest)
WSBuiltInKeywords.verifyResponseStatusCode(buildResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

