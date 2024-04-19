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

// Step 1: Create a new User with missing required fields
user_payload = '{"configs": {}, "trialExpirationDate": "2022-12-31T23:59:59Z", "sessionTimeout": 600, "businessUser": true}'
user_request = new RequestObject()
user_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(user_payload)))
user_request.setRestUrl("https://testops.katalon.io/api/v1/users/add")
user_request.setRestRequestMethod("POST")
addAuthHeader(user_request)
addContentTypeHeader(user_request)
user_response = WSBuiltInKeywords.sendRequest(user_request)

// Step 2: Upload an avatar image to a specific path on the server
uploaded_path = "/path/to/avatar.jpg"
avatar_payload = '{"uploadedPath": "' + uploaded_path + '"}'
avatar_request = new RequestObject()
avatar_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(avatar_payload)))
avatar_request.setRestUrl("https://testops.katalon.io/api/v1/users/avatar")
avatar_request.setRestRequestMethod("POST")
addAuthHeader(avatar_request)
addContentTypeHeader(avatar_request)
avatar_response = WSBuiltInKeywords.sendRequest(avatar_request)

// Step 3: Verify that the response status code is 200
if (WSBuiltInKeywords.verifyResponseStatusCode(user_response, 200)) {
    println("Test Passed: Status code is 200")
} else {
    println("Test Failed: Status code is not 200")
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
