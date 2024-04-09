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

def payload_step1 = '{"code": "inactive_account", "state": "inactive"}'
def request_step1 = new RequestObject()
request_step1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_step1)))
request_step1.setRestUrl("https://v3.recurly.com/accounts")
request_step1.setRestRequestMethod("POST")
addAuthHeader(request_step1)
addContentTypeHeader(request_step1)
def response_step1 = WSBuiltInKeywords.sendRequest(request_step1)
def account_id = new JsonSlurper().parseText(response_step1.getResponseText())['id']

def request_step3 = new RequestObject()
request_step3.setRestUrl("https://v3.recurly.com/accounts/${account_id}/reactivate")
request_step3.setRestRequestMethod("POST")
addAuthHeader(request_step3)
def response_step3 = WSBuiltInKeywords.sendRequest(request_step3)

assert WSBuiltInKeywords.verifyResponseStatusCode(response_step3, 422)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}