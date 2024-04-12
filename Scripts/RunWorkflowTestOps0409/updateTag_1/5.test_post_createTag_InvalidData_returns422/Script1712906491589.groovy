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

def request1 = new RequestObject()
request1.setRestUrl("https://testops.katalon.io/api/v1/organizations")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def body1 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestOrg", "role": "OWNER"])))
request1.setBodyContent(body1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def organization_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

def request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/teams")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def body2 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestTeam", "role": "OWNER", "organizationId": organization_id])))
request2.setBodyContent(body2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def team_id = new JsonSlurper().parseText(response2.getResponseText())["id"]

def request3 = new RequestObject()
request3.setRestUrl("https://testops.katalon.io/api/v1/projects")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
def body3 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestProject", "teamId": team_id])))
request3.setBodyContent(body3)
def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

def request4 = new RequestObject()
request4.setRestUrl("https://testops.katalon.io/api/v1/tags")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
def body4 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["invalid_field": "invalid_data"])))
request4.setBodyContent(body4)
def response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 422)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
