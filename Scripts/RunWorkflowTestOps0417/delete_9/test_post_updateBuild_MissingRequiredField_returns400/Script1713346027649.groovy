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

// Step 1: Create a new Build
def buildPayload = '{"projectId": 1, "project": "Project__unique__", "releaseId": 1, "release": "Release__unique__", "buildStatistics": "BuildStatistics__unique__", "name": "Build__unique__", "description": "Build Description", "date": "2022-01-01T00:00:00Z"}'
def createBuildRequest = new RequestObject()
createBuildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(buildPayload)))
createBuildRequest.setRestUrl("https://testops.katalon.io/api/v1/build")
createBuildRequest.setRestRequestMethod("POST")
addAuthHeader(createBuildRequest)
addContentTypeHeader(createBuildRequest)
def createBuildResponse = WSBuiltInKeywords.sendRequest(createBuildRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createBuildResponse, 200)

def buildId = new JsonSlurper().parseText(createBuildResponse.getResponseText())['id']

// Step 2: Update the Build with missing required field
def updatePayload = '{"projectId": 1, "project": "Project__unique__", "releaseId": 1, "release": "Release__unique__", "buildStatistics": "BuildStatistics__unique__", "name": "Build__unique__", "date": "2022-01-01T00:00:00Z"}'
def updateBuildRequest = new RequestObject()
updateBuildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatePayload)))
updateBuildRequest.setRestUrl("https://testops.katalon.io/api/v1/build/${buildId}")
updateBuildRequest.setRestRequestMethod("PUT")
addAuthHeader(updateBuildRequest)
addContentTypeHeader(updateBuildRequest)
def updateBuildResponse = WSBuiltInKeywords.sendRequest(updateBuildRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateBuildResponse, 400)

if (updateBuildResponse.getStatusCode() == 400) {
	println("Step 3 - Response status code is 400 - Test Passed")
} else {
	println("Step 3 - Test Failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
