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

def orgRequest = new RequestObject()
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"id": 1, "name": "Organization1__unique__", "role": "OWNER"}'))
orgRequest.setBodyContent(orgPayload)
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

def tagRequest = new RequestObject()
tagRequest.setRestUrl("https://testops.katalon.io/api/v1/tags")
tagRequest.setRestRequestMethod("POST")
addAuthHeader(tagRequest)
addContentTypeHeader(tagRequest)
def tagPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"id": 1, "name": "Tag1__unique__", "organizationId": 1}'))
tagRequest.setBodyContent(tagPayload)
def tagResponse = WSBuiltInKeywords.sendRequest(tagRequest)
WSBuiltInKeywords.verifyResponseStatusCode(tagResponse, 400)

if (tagResponse.getStatusCode() == 400) {
    println("Step 3 - Verification: Status Code 400 - Test Passed")
} else {
    println("Step 3 - Verification: Status Code not 400 - Test Failed")
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
