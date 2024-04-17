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
project_payload = '{"name": "Test Project", "teamId": 999}'
project_request = new RequestObject()
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 404)
project_id = new JsonSlurper().parseText(project_response.getResponseText()).id

// Step 2
comment_payload = '{"objectId": 123, "projectId": project_id, "content": "Test comment", "teamId": 999, "objectType": "TEST_CASE", "bySystem": false}'
comment_request = new RequestObject()
comment_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(comment_payload)))
comment_request.setRestUrl("https://testops.katalon.io/api/v1/comments")
comment_request.setRestRequestMethod("POST")
addAuthHeader(comment_request)
addContentTypeHeader(comment_request)
comment_response = WSBuiltInKeywords.sendRequest(comment_request)
WSBuiltInKeywords.verifyResponseStatusCode(comment_response, 404)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}

