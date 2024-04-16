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

def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)

def teamPayload = '{"name": "Test Team", "role": "OWNER", "organizationId": 123}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def k8sAgentRequest = new RequestObject()
k8sAgentRequest.setRestUrl("https://testops.katalon.io/api/v1/k8s-agent")
k8sAgentRequest.setRestRequestMethod("POST")
addAuthHeader(k8sAgentRequest)
addContentTypeHeader(k8sAgentRequest)

def k8sAgentPayload = '{"id": 1, "name": "Test K8S Agent", "certificateAuthority": "ca__unique__", "url": "url__unique__", "namespace": "namespace__unique__", "username": "username__unique__", "password": "password__unique__", "cluster": "cluster__unique__", "region": "region__unique__", "accessKey": "accessKey__unique__", "privateAccessKey": "privateAccessKey__unique__", "teamId": 1, "apiKey": "apiKey__unique__", "authenticationType": "BASIC_AUTH"}'
k8sAgentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(k8sAgentPayload)))

def k8sAgentResponse = WSBuiltInKeywords.sendRequest(k8sAgentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(k8sAgentResponse, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
