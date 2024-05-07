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

def searchRequestPayload = '{"searchRequestFunctions": ["function1__unique__", "function2__unique__"]}'

def searchRequest = new RequestObject()
searchRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(searchRequestPayload)))
searchRequest.setRestUrl("https://testops.katalon.io/api/v1/search")
searchRequest.setRestRequestMethod("POST")
addAuthHeader(searchRequest)
addContentTypeHeader(searchRequest)

def response = WSBuiltInKeywords.sendRequest(searchRequest)
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
