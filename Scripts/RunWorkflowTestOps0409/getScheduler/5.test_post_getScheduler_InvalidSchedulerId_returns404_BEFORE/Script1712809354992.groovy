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

def payload = '{"name": "Test Scheduler", "startTime": "2022-01-01T00:00:00Z", "nextTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T00:00:00Z", "active": true, "interval": 60, "intervalUnit": "MINUTE", "runConfigurationId": 12345}'
def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(payload))
request1.setRestUrl("https://testops.katalon.io/api/v1/test-projects/123/schedulers")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def invalid_scheduler_id = 999999
def request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/test-projects/123/schedulers/${invalid_scheduler_id}")
request2.setRestRequestMethod("GET")
addAuthHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 404)
