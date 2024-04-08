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

// Step 1: Create a new Address
address_payload = '{"phone": "1234567890__unique__", "street1": "123 Main St", "city": "San Francisco", "region": "CA", "postal_code": "94105", "country": "US"}'
address_request = new RequestObject()
address_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(address_payload)))
address_request.setRestUrl("https://v3.recurly.com/addresses")
address_request.setRestRequestMethod("POST")
addAuthHeader(address_request)
addContentTypeHeader(address_request)
address_response = WSBuiltInKeywords.sendRequest(address_request)
WSBuiltInKeywords.verifyResponseStatusCode(address_response, 201)

// Step 2: Create new Settings
settings_payload = '{"billing_address_requirement": "full", "accepted_currencies": ["USD"], "default_currency": "USD"}'
settings_request = new RequestObject()
settings_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(settings_payload)))
settings_request.setRestUrl("https://v3.recurly.com/settings")
settings_request.setRestRequestMethod("POST")
addAuthHeader(settings_request)
addContentTypeHeader(settings_request)
settings_response = WSBuiltInKeywords.sendRequest(settings_request)
WSBuiltInKeywords.verifyResponseStatusCode(settings_response, 201)

// Step 3: Create a new Site
site_payload = '{"subdomain": "example__unique__", "mode": "development", "address": ' + address_payload + ', "settings": ' + settings_payload + '}'
site_request = new RequestObject()
site_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(site_payload)))
site_request.setRestUrl("https://v3.recurly.com/sites")
site_request.setRestRequestMethod("POST")
addAuthHeader(site_request)
addContentTypeHeader(site_request)
site_response = WSBuiltInKeywords.sendRequest(site_request)
WSBuiltInKeywords.verifyResponseStatusCode(site_response, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
