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

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"name": "Project__unique__", "teamId": 123, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())['id']

def customFieldRequest = new RequestObject()
customFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions/create")
customFieldRequest.setRestRequestMethod("POST")
addAuthHeader(customFieldRequest)
addContentTypeHeader(customFieldRequest)
def customFieldPayload = '{"key": "Key__unique__", "displayName": "Display Name__unique__", "projectId": ' + projectId + ', "organizationId": 456, "customFieldOptions": []}'
customFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(customFieldPayload)))
def customFieldResponse = WSBuiltInKeywords.sendRequest(customFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(customFieldResponse, 200)

def invalidCustomFieldPayload = '{"key": "Updated Key__unique__", "displayName": "Updated Display Name__unique__", "projectId": ' + projectId + ', "organizationId": 456, "customFieldOptions": []}'
def invalidCustomFieldRequest = new RequestObject()
invalidCustomFieldRequest.setRestUrl("https://testops.katalon.io/api/v1/custom-field-definitions")
invalidCustomFieldRequest.setRestRequestMethod("PUT")
addAuthHeader(invalidCustomFieldRequest)
addContentTypeHeader(invalidCustomFieldRequest)
invalidCustomFieldRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(invalidCustomFieldPayload)))
def invalidCustomFieldResponse = WSBuiltInKeywords.sendRequest(invalidCustomFieldRequest)
WSBuiltInKeywords.verifyResponseStatusCode(invalidCustomFieldResponse, 400)

assert invalidCustomFieldResponse.getStatusCode() == 400

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
