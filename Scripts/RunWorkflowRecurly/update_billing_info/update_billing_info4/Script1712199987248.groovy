import internal.GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords

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

def account_id = "test_account_id"

def account_payload = [
	"code": "test_account_code",
	"email": "test@example.com",
	"first_name": "John",
	"last_name": "Doe"
]

def api_url = "https://v3.recurly.com/accounts/${account_id}/billing_info"
def invalid_payload = "{invalid_json}"

def request = new RequestObject()
request.setRestUrl(api_url)
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(invalid_payload)))

def response = WSBuiltInKeywords.sendRequest(request)

WSBuiltInKeywords.verifyResponseStatusCode(response, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

