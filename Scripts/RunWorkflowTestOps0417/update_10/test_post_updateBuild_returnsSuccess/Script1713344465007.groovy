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

def payload_step1 = '{"projectId": 1, "releaseId": 1, "name": "BuildName__unique__", "description": "BuildDescription__unique__", "date": "2022-01-01T00:00:00Z"}'
def request_step1 = new RequestObject()
request_step1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_step1)))
request_step1.setRestUrl("https://testops.katalon.io/api/v1/build")
request_step1.setRestRequestMethod("POST")
addAuthHeader(request_step1)
addContentTypeHeader(request_step1)
def response_step1 = WSBuiltInKeywords.sendRequest(request_step1)
def build_id = new JsonSlurper().parseText(response_step1.getResponseText())["id"]

def payload_step3 = '{"projectId": 1, "releaseId": 1, "name": "UpdatedBuildName__unique__", "description": "UpdatedBuildDescription__unique__", "date": "2022-01-01T00:00:00Z"}'
def request_step3 = new RequestObject()
request_step3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_step3)))
request_step3.setRestUrl("https://testops.katalon.io/api/v1/build/" + build_id)
request_step3.setRestRequestMethod("PUT")
addAuthHeader(request_step3)
addContentTypeHeader(request_step3)
def response_step3 = WSBuiltInKeywords.sendRequest(request_step3)

assert WSBuiltInKeywords.verifyResponseStatusCode(response_step3, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

