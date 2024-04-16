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

def step1Request = new RequestObject()
step1Request.setRestUrl("https://testops.katalon.io/api/v1/teams")
step1Request.setRestRequestMethod("POST")
addAuthHeader(step1Request)
addContentTypeHeader(step1Request)
def step1Payload = '{"name": "Test Team", "role": "OWNER", "organizationId": 123}'
step1Request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(step1Payload)))
def step1Response = WSBuiltInKeywords.sendRequest(step1Request)
WSBuiltInKeywords.verifyResponseStatusCode(step1Response, 200)

def step2Request = new RequestObject()
step2Request.setRestUrl("https://testops.katalon.io/api/v1/k8s-agent")
step2Request.setRestRequestMethod("POST")
addAuthHeader(step2Request)
addContentTypeHeader(step2Request)
def step2Payload = '{"username": "test_username__unique__", "token": "test_token__unique__", "accessKey": "test_accessKey__unique__", "privateAccessKey": "test_privateAccessKey__unique__", "teamId": 123}'
step2Request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(step2Payload)))
def step2Response = WSBuiltInKeywords.sendRequest(step2Request)
WSBuiltInKeywords.verifyResponseStatusCode(step2Response, 200)

def k8sAgentId = new JsonSlurper().parseText(step2Response.getResponseText()).id

def step3Request = new RequestObject()
step3Request.setRestUrl("https://testops.katalon.io/api/v1/k8s-agent/${k8sAgentId}")
step3Request.setRestRequestMethod("PUT")
addAuthHeader(step3Request)
addContentTypeHeader(step3Request)
def step3Payload = '{"username": "updated_test_username__unique__", "token": "updated_test_token__unique__", "accessKey": "updated_test_accessKey__unique__", "privateAccessKey": "updated_test_privateAccessKey__unique__", "teamId": 123}'
step3Request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(step3Payload)))
def step3Response = WSBuiltInKeywords.sendRequest(step3Request)
WSBuiltInKeywords.verifyResponseStatusCode(step3Response, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
