import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1
def testProjectRequest = new RequestObject()
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(test_project_payload))))
testProjectRequest.setRestUrl("${base_url}/api/v1/test-projects/sample-git-test-project?projectId=1&type=GIT")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)

def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

// Step 2
def testProjectPublishRequest = new RequestObject()
testProjectPublishRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(test_project_publish_payload))))
testProjectPublishRequest.setRestUrl("${base_url}/api/v1/test-management/test-projects/1/publish")
testProjectPublishRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectPublishRequest)
addContentTypeHeader(testProjectPublishRequest)

def testProjectPublishResponse = WSBuiltInKeywords.sendRequest(testProjectPublishRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectPublishResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
