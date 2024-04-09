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

def request1 = new RequestObject()
request1.setRestUrl("https://v3.recurly.com/accounts")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def payload1 = '{"code": "test_account__unique__"}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 400)

def account_id = null
if (response1.getStatusCode() == 400) {
	def responseJson1 = new JsonSlurper().parseText(response1.getResponseText())
	if (responseJson1.containsKey("id")) {
		account_id = responseJson1["id"]
	}
}

if (account_id) {
	def request2 = new RequestObject()
	request2.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/reactivate")
	request2.setRestRequestMethod("POST")
	addAuthHeader(request2)
	addContentTypeHeader(request2)
	def response2 = WSBuiltInKeywords.sendRequest(request2)
	WSBuiltInKeywords.verifyResponseStatusCode(response2, 400)
	println(response2.getStatusCode())
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}