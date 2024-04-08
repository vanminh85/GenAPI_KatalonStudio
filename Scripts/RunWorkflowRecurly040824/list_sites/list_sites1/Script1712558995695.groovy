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

// Step 1: Create a new Address
def addressRequest = new RequestObject()
addressRequest.setRestUrl("https://v3.recurly.com/accounts/${account_id}/shipping_addresses")
addressRequest.setRestRequestMethod("POST")
addAuthHeader(addressRequest)
addContentTypeHeader(addressRequest)

def addressData = '{"phone": "1234567890", "street1": "123 Main St", "city": "New York", "region": "NY", "postal_code": "10001", "country": "US"}'
addressRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(addressData)))

def addressResponse = WSBuiltInKeywords.sendRequest(addressRequest)
WSBuiltInKeywords.verifyResponseStatusCode(addressResponse, 200)

def addressId = new JsonSlurper().parseText(addressResponse.getResponseText())["id"]

// Step 2: Create a new Site
def siteRequest = new RequestObject()
siteRequest.setRestUrl("https://v3.recurly.com/sites")
siteRequest.setRestRequestMethod("POST")
addAuthHeader(siteRequest)
addContentTypeHeader(siteRequest)

def siteData = '{"subdomain": "testsite", "public_api_key": "abc123", "mode": "development", "address": {"phone": "1234567890", "street1": "123 Main St", "city": "New York", "region": "NY", "postal_code": "10001", "country": "US"}}'
siteRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(siteData)))

def siteResponse = WSBuiltInKeywords.sendRequest(siteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(siteResponse, 201)

def siteId = new JsonSlurper().parseText(siteResponse.getResponseText())["id"]

// Step 3: Send a POST request to /sites with the created Site data
def updateSiteRequest = new RequestObject()
updateSiteRequest.setRestUrl("https://v3.recurly.com/sites/${siteId}")
updateSiteRequest.setRestRequestMethod("POST")
addAuthHeader(updateSiteRequest)
addContentTypeHeader(updateSiteRequest)
updateSiteRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(siteData)))

def updateSiteResponse = WSBuiltInKeywords.sendRequest(updateSiteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateSiteResponse, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
