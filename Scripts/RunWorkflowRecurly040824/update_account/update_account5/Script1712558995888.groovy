import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput

// Import GlobalVariable
import internal.GlobalVariable

// Define addAuthHeader function
def addAuthHeader(request) {
    def authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
    if (authToken) {
        def auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
        request.getHttpHeaderProperties().add(auth_header)
    }
}

// Define addContentTypeHeader function
def addContentTypeHeader(request) {
    def content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
    request.getHttpHeaderProperties().add(content_type_header)
}

// Define uuid variable
uuid = UUID.randomUUID().toString()

// Step 1: Create a new account with an invalid account ID format
def account_payload = [
    "code": "invalid_account_id_format",
    "email": "test@example.com",
    "first_name": "John",
    "last_name": "Doe"
]
def create_account_request = new RequestObject()
create_account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(account_payload))))
create_account_request.setRestUrl("https://v3.recurly.com/accounts")
create_account_request.setRestRequestMethod("POST")
addAuthHeader(create_account_request)
addContentTypeHeader(create_account_request)
def create_account_response = WSBuiltInKeywords.sendRequest(create_account_request)

// Step 2: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(create_account_response, 400)

println("Test case passed")

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}

