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

// Step 1: Create a new Execution Test Result
def requestStep1 = new RequestObject()
requestStep1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(payload))))
requestStep1.setRestUrl("${base_url}/api/v1/test-results/${id}/custom-fields")
requestStep1.setRestRequestMethod("POST")
addAuthHeader(requestStep1)
addContentTypeHeader(requestStep1)
def responseStep1 = WSBuiltInKeywords.sendRequest(requestStep1)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep1, 200)

// Step 2: Retrieve the details of the created Execution Test Result
def requestStep2 = new RequestObject()
requestStep2.setRestUrl("${base_url}/api/v1/test-results/${id}/custom-fields")
requestStep2.setRestRequestMethod("GET")
addAuthHeader(requestStep2)
def responseStep2 = WSBuiltInKeywords.sendRequest(requestStep2)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep2, 200)

// Step 3: Verify the response status code is 200
if (responseStep2.getStatusCode() == 200) {
	println("Step 3 - Verification: PASS")
} else {
	println("Step 3 - Verification: FAIL")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

