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
organizationRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations/{id}/trial-request?id=1&feature=KSE")
organizationRequest.setRestRequestMethod("POST")
addAuthHeader(organizationRequest)
addContentTypeHeader(organizationRequest)
def organizationResponse = WSBuiltInKeywords.sendRequest(organizationRequest)
WSBuiltInKeywords.verifyResponseStatusCode(organizationResponse, 200)
def organizationId = new JsonSlurper().parseText(organizationResponse.getResponseText())["organization"]["id"]

def projectRequest = new RequestObject()
def projectPayload = '{"name": "Project__unique__", "teamId": 1, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": false}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)
def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

def tagRequest = new RequestObject()
def tagPayload = '{"name": "Tag__unique__", "projectId": ' + projectId + ', "organizationId": ' + organizationId + '}'
tagRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(tagPayload)))
tagRequest.setRestUrl("https://testops.katalon.io/api/v1/tags")
tagRequest.setRestRequestMethod("POST")
addAuthHeader(tagRequest)
addContentTypeHeader(tagRequest)
def tagResponse = WSBuiltInKeywords.sendRequest(tagRequest)
WSBuiltInKeywords.verifyResponseStatusCode(tagResponse, 201)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}