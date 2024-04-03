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

// Step 1: Create a new account
def account_payload = [
    "code": "test_account",
    "email": "test@example.com",
    "first_name": "John",
    "last_name": "Doe"
]

def account_request = new RequestObject()
account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(account_payload))))
account_request.setRestUrl("https://v3.recurly.com/accounts")
account_request.setRestRequestMethod("POST")
addAuthHeader(account_request)
addContentTypeHeader(account_request)

def account_response = WSBuiltInKeywords.sendRequest(account_request)
WSBuiltInKeywords.verifyResponseStatusCode(account_response, 201)

def account_id = new JsonSlurper().parseText(account_response.getResponseText())["id"]

// Step 2: Create billing information for the account
def billing_info_payload = [
    "token_id": "test_token",
    "first_name": "John",
    "last_name": "Doe",
    "address": [
        "street1": "123 Main St",
        "city": "New York",
        "state": "NY",
        "postal_code": "10001",
        "country": "US"
    ]
]

def billing_info_request = new RequestObject()
billing_info_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(billing_info_payload))))
billing_info_request.setRestUrl("https://v3.recurly.com/accounts/${account_id}/billing_info")
billing_info_request.setRestRequestMethod("POST")
addAuthHeader(billing_info_request)
addContentTypeHeader(billing_info_request)

def billing_info_response = WSBuiltInKeywords.sendRequest(billing_info_request)
WSBuiltInKeywords.verifyResponseStatusCode(billing_info_response, 200)

// Step 3: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(billing_info_response, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}

