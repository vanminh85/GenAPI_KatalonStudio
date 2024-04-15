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

def userRequest = new RequestObject()
userRequest.setRestUrl("https://testops.katalon.io/api/v1/users")
userRequest.setRestRequestMethod("POST")
addAuthHeader(userRequest)
addContentTypeHeader(userRequest)
def userPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"firstName": "John", "lastName": "Doe"}'))
userRequest.setBodyContent(userPayload)
def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userResponse, 200)

def organizationRequest = new RequestObject()
organizationRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
organizationRequest.setRestRequestMethod("POST")
addAuthHeader(organizationRequest)
addContentTypeHeader(organizationRequest)
def organizationPayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Organization__unique__", "role": "OWNER"}'))
organizationRequest.setBodyContent(organizationPayload)
def organizationResponse = WSBuiltInKeywords.sendRequest(organizationRequest)
WSBuiltInKeywords.verifyResponseStatusCode(organizationResponse, 200)

def userOrgFeatureRequest = new RequestObject()
userOrgFeatureRequest.setRestUrl("https://testops.katalon.io/api/v1/notifications/users")
userOrgFeatureRequest.setRestRequestMethod("POST")
addAuthHeader(userOrgFeatureRequest)
addContentTypeHeader(userOrgFeatureRequest)
def userOrgFeaturePayload = new HttpTextBodyContent(replaceSuffixWithUUID('{"userId": 123, "organizationId": 456, "userEmail": "user@example.com", "feature": "KSE"}'))
userOrgFeatureRequest.setBodyContent(userOrgFeaturePayload)
def userOrgFeatureResponse = WSBuiltInKeywords.sendRequest(userOrgFeatureRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userOrgFeatureResponse, 409)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
