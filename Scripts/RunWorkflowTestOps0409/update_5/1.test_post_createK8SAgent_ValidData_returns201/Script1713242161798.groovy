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

def teamPayload = '{"name": "Test Team", "role": "OWNER", "organizationId": 123}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText()).id

def k8sAgentPayload = '{"username": "username__unique__", "token": "token__unique__", "accessKey": "accessKey__unique__", "privateAccessKey": "privateAccessKey__unique__", "teamId": ' + teamId + '}'
def k8sAgentRequest = new RequestObject()
k8sAgentRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(k8sAgentPayload)))
k8sAgentRequest.setRestUrl("https://testops.katalon.io/api/v1/k8s-agent")
k8sAgentRequest.setRestRequestMethod("POST")
addAuthHeader(k8sAgentRequest)
addContentTypeHeader(k8sAgentRequest)
def k8sAgentResponse = WSBuiltInKeywords.sendRequest(k8sAgentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(k8sAgentResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
