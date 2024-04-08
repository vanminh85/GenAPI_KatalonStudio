import internal.GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
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

def base_url = "https://v3.recurly.com"

// Step 1: Call the API POST /accounts/{account_id}/acquisition
def account_id = "valid_account_id"
def url = "${base_url}/accounts/${account_id}/acquisition"
def request = new RequestObject()
request.setRestUrl(url)
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)
def payload = "{}"
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))

def response = WSBuiltInKeywords.sendRequest(request)

// Step 2: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(response, 400)

println("Test case passed: test_post_missingAccountAcquisitionUpdate_returns400")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
