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

// Step 1: Create a new account without providing a required field
account_payload = """
{
	    "acquisition": {
		    "username": "test_account",
		    "email": "test@example.com",
		    "preferred_locale": "en-US",
		    "preferred_time_zone": "America/Los_Angeles",
		    "cc_emails": "test_cc@example.com",
		    "first_name": "John",
		    "last_name": "Doe",
		    "company": "Test Company",
		    "tax_exempt": false,
		    "address": {
		        "phone": "1234567890",
		        "street1": "123 Main St",
		        "city": "Los Angeles",
		        "region": "CA",
		        "postal_code": "90001",
		        "country": "US"
	    }
   }
}
"""

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(account_payload)))
request.setRestUrl(base_url + "/accounts")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addAcceptHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 400)
println(response.getStatusCode())

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
