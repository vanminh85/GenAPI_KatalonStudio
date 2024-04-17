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
		auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Request 1
request1 = new RequestObject()
request1.setRestUrl("https://testops.katalon.io/api/v1/build/nonExistentId")
request1.setRestRequestMethod("PUT")
addAuthHeader(request1)
addContentTypeHeader(request1)
payload1 = '{"name": "TestBuild__unique__"}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))

response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 404)

// Step 2
println(response1.getStatusCode())

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

