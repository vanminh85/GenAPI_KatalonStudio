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

def accountData = '''
{
    "code": "test_code__unique__",
    "acquisition": {"source": "test_source__unique__"},
    "external_accounts": [{"account_number": "1234567890"}],
    "shipping_addresses": [{"name": "John Doe", "address": "123 Main St", "city": "Anytown", "state": "CA", "zip": "12345", "country": "US"}]
}
'''

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(accountData)))
request.setRestUrl("https://v3.recurly.com/accounts")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
