import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
    authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
    if (authToken) {
        auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
        request.getHttpHeaderProperties().add(auth_header)
    }
}

def addContentTypeHeader(request) {
    content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
    request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1
request_step1 = new RequestObject()
request_step1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(payload_step1))))
request_step1.setRestUrl("${base_url}/api/v1/test-projects/sample-git-test-project?projectId=1&type=GIT")
request_step1.setRestRequestMethod("POST")
addAuthHeader(request_step1)
addContentTypeHeader(request_step1)

response_step1 = WSBuiltInKeywords.sendRequest(request_step1)
WSBuiltInKeywords.verifyResponseStatusCode(response_step1, 200)

// Step 2
request_step2 = new RequestObject()
request_step2.setRestUrl("${base_url}/api/v1/test-management/test-projects/${non_existing_id}/publish")
request_step2.setRestRequestMethod("POST")
addAuthHeader(request_step2)
addContentTypeHeader(request_step2)

response_step2 = WSBuiltInKeywords.sendRequest(request_step2)
WSBuiltInKeywords.verifyResponseStatusCode(response_step2, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
