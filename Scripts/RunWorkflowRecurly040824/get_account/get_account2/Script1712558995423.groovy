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

// Request 1
def request1 = new RequestObject()
request1.setRestUrl("https://v3.recurly.com/accounts")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)

def payload1 = '''
{
    "username": "test_username__unique__",
    "email": "test_email__unique__@example.com",
    "preferred_locale": "en-US",
    "preferred_time_zone": "America/Los_Angeles",
    "cc_emails": "test_cc_email__unique__@example.com",
    "first_name": "Test",
    "last_name": "User",
    "company": "Test Company",
    "vat_number": "VAT123",
    "tax_exempt": false,
    "exemption_certificate": "exemption123",
    "parent_account_id": "parent123",
    "bill_to": "self",
    "dunning_campaign_id": "dunning123",
    "invoice_template_id": "template123",
    "address": {
        "street1": "123 Test St",
        "city": "Test City",
        "state": "CA",
        "zip": "12345",
        "country": "US"
    },
    "billing_info": {
        "first_name": "Test",
        "last_name": "User",
        "number": "4111111111111111",
        "month": "12",
        "year": "2023"
    }
}
'''
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))

def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 400)


def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
