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

def testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
def testProjectPayload = '{"name": "TestProject__unique__", "projectId": 123, "teamId": 456}'
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
def testProjectId = new JsonSlurper().parseText(testProjectResponse.getResponseText()).get("id")

def updateScriptRequest = new RequestObject()
updateScriptRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/${testProjectId}/update-script-repo")
updateScriptRequest.setRestRequestMethod("POST")
addAuthHeader(updateScriptRequest)
addContentTypeHeader(updateScriptRequest)
def updateScriptPayload = '{"batch": "batch__unique__", "folderPath": "folderPath__unique__", "uploadedPath": "uploadedPath__unique__"}'
updateScriptRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateScriptPayload)))
def updateScriptResponse = WSBuiltInKeywords.sendRequest(updateScriptRequest)

if (!WSBuiltInKeywords.verifyResponseStatusCode(updateScriptResponse, 200)) {
	println("Step 3 - Response status code is not 200")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

