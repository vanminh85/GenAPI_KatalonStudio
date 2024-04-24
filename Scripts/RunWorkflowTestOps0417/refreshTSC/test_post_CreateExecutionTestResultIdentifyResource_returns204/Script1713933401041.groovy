import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
	if (authToken) {
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1
requestStep1 = new RequestObject()
requestStep1.setRestUrl("https://testops.katalon.io/api/v1/test-results/123/incidents")
requestStep1.setRestRequestMethod("POST")
addAuthHeader(requestStep1)
addContentTypeHeader(requestStep1)
payloadStep1 = '{"executionId": 12345}'
requestStep1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payloadStep1)))
responseStep1 = WSBuiltInKeywords.sendRequest(requestStep1)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep1, 200)

// Step 2
requestStep2 = new RequestObject()
requestStep2.setRestUrl("https://testops.katalon.io/api/v1/test-projects/123/refresh-tsc")
requestStep2.setRestRequestMethod("POST")
addAuthHeader(requestStep2)
addContentTypeHeader(requestStep2)
payloadStep2 = '{"executionId": 67890}'
requestStep2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payloadStep2)))
responseStep2 = WSBuiltInKeywords.sendRequest(requestStep2)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep2, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

