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
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def releaseRequest = new RequestObject()
releaseRequest.setRestUrl("https://testops.katalon.io/api/v1/releases")
releaseRequest.setRestRequestMethod("POST")
addAuthHeader(releaseRequest)
addContentTypeHeader(releaseRequest)
def releasePayload = '{"name": "Release__unique__", "startTime": "2022-01-01", "endTime": "2022-01-31", "description": "New release", "projectId": 1, "closed": false, "createdAt": "2022-01-01T00:00:00Z", "releaseStatus": "READY"}'
releaseRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(releasePayload)))
def releaseResponse = WSBuiltInKeywords.sendRequest(releaseRequest)
WSBuiltInKeywords.verifyResponseStatusCode(releaseResponse, 200)

def buildRequest = new RequestObject()
buildRequest.setRestUrl("https://testops.katalon.io/api/v1/build")
buildRequest.setRestRequestMethod("POST")
addAuthHeader(buildRequest)
addContentTypeHeader(buildRequest)
def buildPayload = '{"projectId": 1, "project": {"id": 1}, "releaseId": 1, "release": {"id": 1}, "buildStatistics": {"id": 1}, "name": "Build__unique__", "description": "New build", "date": "2022-01-01T00:00:00Z"}'
buildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(buildPayload)))
def buildResponse = WSBuiltInKeywords.sendRequest(buildRequest)
WSBuiltInKeywords.verifyResponseStatusCode(buildResponse, 200)
def buildId = new JsonSlurper().parseText(buildResponse.getResponseText())["id"]

def updateBuildRequest = new RequestObject()
updateBuildRequest.setRestUrl("https://testops.katalon.io/api/v1/build/${buildId}")
updateBuildRequest.setRestRequestMethod("PUT")
addAuthHeader(updateBuildRequest)
addContentTypeHeader(updateBuildRequest)
def updateBuildPayload = '{"projectId": 1, "project": {"id": 1}, "releaseId": 1, "release": {"id": 1}, "buildStatistics": {"id": 1}, "name": "Updated Build__unique__", "description": "Updated build", "date": "2022-01-02T00:00:00Z"}'
updateBuildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateBuildPayload)))
def updateBuildResponse = WSBuiltInKeywords.sendRequest(updateBuildRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateBuildResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

