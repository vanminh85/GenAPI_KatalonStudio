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

def buildPayload = '{"projectId": 1, "releaseId": 1, "name": "Test Build__' + uuid + '__", "description": "Test Build Description__' + uuid + '__", "date": "2022-01-01T00:00:00Z"}'

def buildRequest = new RequestObject()
buildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(buildPayload)))
buildRequest.setRestUrl("https://testops.katalon.io/api/v1/build")
buildRequest.setRestRequestMethod("POST")
addAuthHeader(buildRequest)
addContentTypeHeader(buildRequest)

def buildResponse = WSBuiltInKeywords.sendRequest(buildRequest)
def buildId = new JsonSlurper().parseText(buildResponse.getResponseText())["id"]

def deleteRequest = new RequestObject()
deleteRequest.setRestUrl("https://testops.katalon.io/api/v1/build/" + buildId)
deleteRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteRequest)

def deleteResponse = WSBuiltInKeywords.sendRequest(deleteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

