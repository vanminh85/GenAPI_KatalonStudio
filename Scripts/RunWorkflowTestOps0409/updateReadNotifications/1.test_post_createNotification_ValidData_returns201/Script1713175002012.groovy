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
def userPayload = '{"email": "test@example.com", "firstName": "John", "lastName": "Doe", "password": "password123", "systemRole": "USER", "sessionTimeout": 3600, "businessUser": false}'
userRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
def userResponse = WSBuiltInKeywords.sendRequest(userRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userResponse, 200)

def orgRequest = new RequestObject()
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
def orgPayload = '{"name": "TestOrg__unique__", "role": "OWNER"}'
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

def userOrgFeatureRequest = new RequestObject()
userOrgFeatureRequest.setRestUrl("https://testops.katalon.io/api/v1/notifications/users")
userOrgFeatureRequest.setRestRequestMethod("POST")
addAuthHeader(userOrgFeatureRequest)
addContentTypeHeader(userOrgFeatureRequest)
def userOrgFeaturePayload = '{"userId": 123, "organizationId": 456, "userEmail": "test@example.com", "feature": "KSE"}'
userOrgFeatureRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userOrgFeaturePayload)))
def userOrgFeatureResponse = WSBuiltInKeywords.sendRequest(userOrgFeatureRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userOrgFeatureResponse, 201)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
