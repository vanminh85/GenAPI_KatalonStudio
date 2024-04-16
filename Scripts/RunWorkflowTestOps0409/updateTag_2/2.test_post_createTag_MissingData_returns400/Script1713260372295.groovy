import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
    authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
    if (authToken) {
        authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
        request.getHttpHeaderProperties().add(authHeader)
    }
}

def addContentTypeHeader(request) {
    contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
    request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new TagResource without providing any data in the request body
tagPayload = '{}'
request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(tagPayload)))
request.setRestUrl("https://testops.katalon.io/api/v1/tags")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

response = WSBuiltInKeywords.sendRequest(request)

// Step 2: Verify that the response status code is 400 Bad Request
if (WSBuiltInKeywords.verifyResponseStatusCode(response, 400)) {
    println("Step 2: PASS - Response status code is 400 Bad Request")
} else {
    println("Step 2: FAIL - Response status code is not 400 Bad Request")
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
