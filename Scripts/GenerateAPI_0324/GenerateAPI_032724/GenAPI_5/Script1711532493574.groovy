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
		def auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, "Basic " + authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addAcceptHeader(request) {
	def accept_header = new TestObjectProperty("Accept", ConditionType.EQUALS, "application/vnd.recurly.v2021-02-25")
	request.getHttpHeaderProperties().add(accept_header)
}

def addContentTypeHeader(request) {
	def content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

def base_url = "https://v3.recurly.com"

// Step 1: Create a new account with a non-existent parent account
def account_payload = [
	"code": "test_account__unique__",
	"parent_account_id": "non_existent_parent_account"
]

def create_account_request = new RequestObject()
create_account_request.setRestUrl(base_url + "/accounts")
create_account_request.setRestRequestMethod("POST")
create_account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(account_payload))))

addAuthHeader(create_account_request)
addAcceptHeader(create_account_request)
addContentTypeHeader(create_account_request)

def create_account_response = WSBuiltInKeywords.sendRequest(create_account_request)

// Step 2: Verify that the response status code is 404
WSBuiltInKeywords.verifyResponseStatusCode(create_account_response, 404)

println("Test case passed")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

