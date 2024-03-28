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

// Step 1: Create a new account with valid data
def account_payload = [
	"code": "test_account__unique__",
	"email": "test@example.com",
	"first_name": "John",
	"last_name": "Doe"
]

def create_account_url = base_url + "/accounts"
def create_account_request = new RequestObject()
create_account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(account_payload))))
create_account_request.setRestUrl(create_account_url)
create_account_request.setRestRequestMethod("POST")
addAuthHeader(create_account_request)
addAcceptHeader(create_account_request)
addContentTypeHeader(create_account_request)

def create_account_response = WSBuiltInKeywords.sendRequest(create_account_request)
def create_account_status_code = create_account_response.getStatusCode()

// Step 2: Verify that the response status code is 201
WSBuiltInKeywords.verifyResponseStatusCode(create_account_response, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('__unique__', uuid)
	return replacedString
}
