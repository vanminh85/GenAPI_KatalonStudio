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

def organizationRequest = new RequestObject()
organizationRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
organizationRequest.setRestRequestMethod("POST")
addAuthHeader(organizationRequest)
addContentTypeHeader(organizationRequest)
organizationRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestOrg", "role": "OWNER"]))))
def organizationResponse = WSBuiltInKeywords.sendRequest(organizationRequest)
WSBuiltInKeywords.verifyResponseStatusCode(organizationResponse, 200)
def organizationId = new JsonSlurper().parseText(organizationResponse.getResponseText())["id"]

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestProject", "teamId": organizationId])))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

def buildRequest = new RequestObject()
buildRequest.setRestUrl("https://testops.katalon.io/api/v1/build")
buildRequest.setRestRequestMethod("POST")
addAuthHeader(buildRequest)
addContentTypeHeader(buildRequest)
buildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["projectId": projectId, "name": "TestBuild"])))
def buildResponse = WSBuiltInKeywords.sendRequest(buildRequest)
WSBuiltInKeywords.verifyResponseStatusCode(buildResponse, 200)
def buildId = new JsonSlurper().parseText(buildResponse.getResponseText())["id"]

def jobRequest = new RequestObject()
jobRequest.setRestUrl("https://testops.katalon.io/api/v1/jobs/update-job")
jobRequest.setRestRequestMethod("POST")
addAuthHeader(jobRequest)
addContentTypeHeader(jobRequest)
jobRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["buildNumber": buildId, "status": "QUEUED"])))
def jobResponse = WSBuiltInKeywords.sendRequest(jobRequest)
WSBuiltInKeywords.verifyResponseStatusCode(jobResponse, 200)
def jobId = new JsonSlurper().parseText(jobResponse.getResponseText())["id"]

def tagRequest = new RequestObject()
tagRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/${jobId}/tags")
tagRequest.setRestRequestMethod("POST")
addAuthHeader(tagRequest)
addContentTypeHeader(tagRequest)
tagRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestTag", "projectId": projectId, "organizationId": organizationId])))
def tagResponse = WSBuiltInKeywords.sendRequest(tagRequest)
WSBuiltInKeywords.verifyResponseStatusCode(tagResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}