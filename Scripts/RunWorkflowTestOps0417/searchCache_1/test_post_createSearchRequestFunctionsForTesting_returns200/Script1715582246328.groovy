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

def searchRequestFunctionPayload = [
	searchRequestFunctions: [
		[
			name: "function_name__unique__",
			type: "function_type__unique__"
		]
	]
]

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(searchRequestFunctionPayload))))
request1.setRestUrl("https://testops.katalon.io/api/v1/search/cache")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)

def response1 = WSBuiltInKeywords.sendRequest(request1)
assert WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def response1Json = new JsonSlurper().parseText(response1.getResponseText())
println("Step 1 - Status Code: " + response1Json.status)

def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(searchRequestFunctionPayload))))
request2.setRestUrl("https://testops.katalon.io/api/v1/search/cache")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)

def response2 = WSBuiltInKeywords.sendRequest(request2)
assert WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def response2Json = new JsonSlurper().parseText(response2.getResponseText())
println("Step 2 - Status Code: " + response2Json.status)

assert response2Json.status == 200
println("Step 3 - Status Code: 200")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}