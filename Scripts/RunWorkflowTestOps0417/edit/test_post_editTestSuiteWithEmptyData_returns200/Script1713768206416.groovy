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

// Request 1: Retrieve an existing Test Suite ID
request1 = new RequestObject()
request1.setRestUrl("https://testops.katalon.io/api/v1/test-suites/123")
request1.setRestRequestMethod("GET")
addAuthHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Request 2: Attempt to update the Test Suite with empty data
request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/test-suites/123/edit")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID("{}")))
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Request 3: Verify that the API responds with status code 200
assert response2.getStatusCode() == 200

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}


