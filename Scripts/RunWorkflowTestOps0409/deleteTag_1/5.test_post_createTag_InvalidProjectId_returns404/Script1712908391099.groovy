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

def organizationRequest = new RequestObject()
organizationRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
organizationRequest.setRestRequestMethod("POST")
addAuthHeader(organizationRequest)
addContentTypeHeader(organizationRequest)
def organizationPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"id": 1, "name": "Organization__unique__"}'))
organizationRequest.setBodyContent(organizationPayload)

def organizationResponse = WSBuiltInKeywords.sendRequest(organizationRequest)
WSBuiltInKeywords.verifyResponseStatusCode(organizationResponse, 200)

def tagRequest = new RequestObject()
tagRequest.setRestUrl("https://testops.katalon.io/api/v1/tags")
tagRequest.setRestRequestMethod("POST")
addAuthHeader(tagRequest)
addContentTypeHeader(tagRequest)
def tagPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"id": 1, "name": "Tag__unique__", "projectId": -1, "organizationId": 1}'))
tagRequest.setBodyContent(tagPayload)

def tagResponse = WSBuiltInKeywords.sendRequest(tagRequest)
WSBuiltInKeywords.verifyResponseStatusCode(tagResponse, 404)

assert tagResponse.getStatusCode() == 404

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
