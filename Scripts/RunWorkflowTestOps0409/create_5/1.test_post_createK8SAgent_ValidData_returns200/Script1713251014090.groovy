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

// Step 1
def teamPayload = '{"name": "Test Team", "role": "OWNER"}'
def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())['id']

// Step 2
def circleciPayload = '{"name": "Test CircleCI Agent", "url": "http://test-url.com", "username": "testuser", "token": "testtoken", "project": "testproject", "vcsType": "GIT", "branch": "main", "teamId": teamId}'
def circleciRequest = new RequestObject()
circleciRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(circleciPayload)))
circleciRequest.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent")
circleciRequest.setRestRequestMethod("POST")
addAuthHeader(circleciRequest)
addContentTypeHeader(circleciRequest)
def circleciResponse = WSBuiltInKeywords.sendRequest(circleciRequest)
WSBuiltInKeywords.verifyResponseStatusCode(circleciResponse, 200)

// Step 3
def k8sPayload = '{"name": "Test K8S Agent", "certificateAuthority": "testCA", "url": "http://test-k8s-url.com", "namespace": "test-namespace", "username": "testuser", "password": "testpassword", "token": "testtoken", "cluster": "test-cluster", "region": "test-region", "accessKey": "test-access-key", "privateAccessKey": "test-private-access-key", "teamId": teamId, "apiKey": "test-api-key", "authenticationType": "BASIC_AUTH"}'
def k8sRequest = new RequestObject()
k8sRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(k8sPayload)))
k8sRequest.setRestUrl("https://testops.katalon.io/api/v1/k8s-agent")
k8sRequest.setRestRequestMethod("POST")
addAuthHeader(k8sRequest)
addContentTypeHeader(k8sRequest)
def k8sResponse = WSBuiltInKeywords.sendRequest(k8sRequest)
WSBuiltInKeywords.verifyResponseStatusCode(k8sResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
