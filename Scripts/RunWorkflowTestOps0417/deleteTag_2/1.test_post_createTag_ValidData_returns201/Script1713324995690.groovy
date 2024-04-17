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
def request1 = new RequestObject()
request1.setRestUrl("https://testops.katalon.io/api/v1/test-projects/sample")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def bodyContent1 = new HttpTextBodyContent(replaceSuffixWithUUID("{'name': 'Sample Project', 'type': 'KS', 'projectId': 123}"))
request1.setBodyContent(bodyContent1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def project_data1 = new JsonSlurper().parseText(response1.getResponseText())
def extracted_projectId1 = project_data1['projectId']

// Step 4
def request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/projects")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def bodyContent2 = new HttpTextBodyContent(replaceSuffixWithUUID("{'name': 'New Project', 'teamId': 456}"))
request2.setBodyContent(bodyContent2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def project_data2 = new JsonSlurper().parseText(response2.getResponseText())
def extracted_projectId2 = project_data2['projectId']

// Step 7
def request3 = new RequestObject()
request3.setRestUrl("https://testops.katalon.io/api/v1/tags")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
def bodyContent3 = new HttpTextBodyContent(replaceSuffixWithUUID("{'name': 'New Tag', 'projectId': ${extracted_projectId2}, 'organizationId': 789}"))
request3.setBodyContent(bodyContent3)
def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 201)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}