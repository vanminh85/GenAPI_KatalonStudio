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

// Request 1: Create a new Site
def sitePayload = [
	id: "existing_site_id__unique__",
	object: "site",
	subdomain: "test_subdomain__unique__",
	public_api_key: "test_public_api_key__unique__",
	mode: "development",
	address: [
		phone: "1234567890",
		street1: "123 Test St",
		street2: "Apt 1",
		city: "Test City",
		region: "Test Region",
		postal_code: "12345",
		country: "US",
		geo_code: "test_geo_code__unique__"
	],
	settings: [
		billing_address_requirement: "full",
		accepted_currencies: ["USD", "EUR"],
		default_currency: "USD"
	],
	features: ["credit_memos"],
	created_at: "2022-01-01T00:00:00Z",
	updated_at: "2022-01-01T00:00:00Z",
	deleted_at: null
]

def siteRequest = new RequestObject()
siteRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(sitePayload))))
siteRequest.setRestUrl("https://v3.recurly.com/sites")
siteRequest.setRestRequestMethod("POST")
addAuthHeader(siteRequest)
addContentTypeHeader(siteRequest)

def siteResponse = WSBuiltInKeywords.sendRequest(siteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(siteResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
