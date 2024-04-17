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

// Step 1: Send a DELETE request to /api/v1/build/{nonExistentId}
def deleteRequest = new RequestObject()
deleteRequest.setRestUrl("https://testops.katalon.io/api/v1/build/999")
deleteRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteRequest)
addContentTypeHeader(deleteRequest)

def deleteResponse = WSBuiltInKeywords.sendRequest(deleteRequest)

// Step 2: Verify that the response status code is 404
def isStatusCode404 = WSBuiltInKeywords.verifyResponseStatusCode(deleteResponse, 404)
if (isStatusCode404) {
	println("Step 2: Response status code is 404 - Test Passed")
} else {
	println("Step 2: Response status code is not 404 - Test Failed")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

