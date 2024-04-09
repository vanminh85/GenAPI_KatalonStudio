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

def requestStep1 = new RequestObject()
requestStep1.setRestUrl("https://v3.recurly.com/accounts/valid_account_id__unique__/reactivate")
requestStep1.setRestRequestMethod("POST")
addAuthHeader(requestStep1)
addContentTypeHeader(requestStep1)

def responseStep1 = WSBuiltInKeywords.sendRequest(requestStep1)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep1, 404)

println("Step 2 - Verify that the response status code is 404")
println("Response status code: " + responseStep1.getStatusCode())

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}