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

// Step 1: Create a new CircleCI agent
def createCircleCIRequest = new RequestObject()
def createCircleCIPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	"name": "CircleCI Agent_${uuid}",
	"url": "https://example.com",
	"username": "circleci_user_${uuid}",
	"token": "circleci_token_${uuid}",
	"project": "project_name_${uuid}",
	"vcsType": "git",
	"branch": "main",
	"teamId": 123,
	"apiKey": "api_key_${uuid}"
])))
createCircleCIRequest.setBodyContent(createCircleCIPayload)
createCircleCIRequest.setRestUrl(GlobalVariable.base_url + "/api/v1/circle-ci-agent")
createCircleCIRequest.setRestRequestMethod("POST")
addAuthHeader(createCircleCIRequest)
addContentTypeHeader(createCircleCIRequest)

def createCircleCIResponse = WSBuiltInKeywords.sendRequest(createCircleCIRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createCircleCIResponse, 200)

def createdCircleCIAgentId = new JsonSlurper().parseText(createCircleCIResponse.getResponseText())["id"]

// Step 3: Delete the CircleCI agent
def deleteCircleCIRequest = new RequestObject()
deleteCircleCIRequest.setRestUrl(GlobalVariable.base_url + "/api/v1/circle-ci-agent/${createdCircleCIAgentId}")
deleteCircleCIRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteCircleCIRequest)

def deleteCircleCIResponse = WSBuiltInKeywords.sendRequest(deleteCircleCIRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteCircleCIResponse, 204)

// Step 4: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(deleteCircleCIResponse, 204)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}


