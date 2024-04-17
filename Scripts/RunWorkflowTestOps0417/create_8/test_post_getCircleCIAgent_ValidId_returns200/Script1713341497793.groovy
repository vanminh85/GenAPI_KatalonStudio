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

def createCircleCIAgent() {
	def createRequest = new RequestObject()
	createRequest.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent")
	createRequest.setRestRequestMethod("POST")
	addAuthHeader(createRequest)
	addContentTypeHeader(createRequest)
	
	def payload = '{"name": "CircleCI Agent__' + uuid + '__", "url": "https://example.com", "username": "circleci_user__' + uuid + '__", "token": "circleci_token__' + uuid + '__", "project": "my_project", "vcsType": "git", "branch": "main", "teamId": 123, "apiKey": "circleci_api_key__' + uuid + '__"}'
	createRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))
	
	def createResponse = WSBuiltInKeywords.sendRequest(createRequest)
	def createdAgentId = new JsonSlurper().parseText(createResponse.getResponseText())["id"]
	
	return createdAgentId
}

def getCircleCIAgent(agentId) {
	def getRequest = new RequestObject()
	getRequest.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent/" + agentId)
	getRequest.setRestRequestMethod("GET")
	addAuthHeader(getRequest)
	
	def getResponse = WSBuiltInKeywords.sendRequest(getRequest)
	
	return getResponse
}

def createdAgentId = createCircleCIAgent()
def getResponse = getCircleCIAgent(createdAgentId)
WSBuiltInKeywords.verifyResponseStatusCode(getResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

