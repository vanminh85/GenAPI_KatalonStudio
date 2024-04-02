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

// Step 1: Create a new Address resource
def address_payload = [
    "phone": "1234567890",
    "street1": "123 Main St",
    "city": "New York",
    "region": "NY",
    "postal_code": "10001",
    "country": "US"
]

def address_request = new RequestObject()
address_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(address_payload))))
address_request.setRestUrl("${base_url}/addresses")
address_request.setRestRequestMethod("POST")
addAuthHeader(address_request)
addContentTypeHeader(address_request)

def address_response = WSBuiltInKeywords.sendRequest(address_request)
WSBuiltInKeywords.verifyResponseStatusCode(address_response, 201)
def address_id = new JsonSlurper().parseText(address_response.getResponseText())["id"]

// Step 2: Create a new Site resource
def site_payload = [
    "address": [
        "id": address_id
    ]
]

def site_request = new RequestObject()
site_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(site_payload))))
site_request.setRestUrl("${base_url}/sites")
site_request.setRestRequestMethod("POST")
addAuthHeader(site_request)
addContentTypeHeader(site_request)

def site_response = WSBuiltInKeywords.sendRequest(site_request)
WSBuiltInKeywords.verifyResponseStatusCode(site_response, 201)
def site_id = new JsonSlurper().parseText(site_response.getResponseText())["id"]

// Step 3: Send a POST request to /sites with the created Site resource
def create_site_payload = [
    "site_id": site_id
]

def create_site_request = new RequestObject()
create_site_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(create_site_payload))))
create_site_request.setRestUrl("${base_url}/sites/${site_id}")
create_site_request.setRestRequestMethod("POST")
addAuthHeader(create_site_request)
addContentTypeHeader(create_site_request)

def create_site_response = WSBuiltInKeywords.sendRequest(create_site_request)
WSBuiltInKeywords.verifyResponseStatusCode(create_site_response, 400)

// Step 4: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(create_site_response, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}

