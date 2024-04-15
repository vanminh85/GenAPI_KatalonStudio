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

// Step 1: Create a new Build
build_payload = '{"projectId": 1, "name": "BuildName__unique__", "description": "BuildDescription__unique__"}'
build_request = new RequestObject()
build_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(build_payload)))
build_request.setRestUrl("https://testops.katalon.io/api/v1/build")
build_request.setRestRequestMethod("POST")
addAuthHeader(build_request)
addContentTypeHeader(build_request)
build_response = WSBuiltInKeywords.sendRequest(build_request)
build_id = new JsonSlurper().parseText(build_response.getResponseText())["id"]

// Step 2: Extract the 'id' of the created Build
// Already extracted in Step 1

// Step 3: Link the Build to a Run Configuration
execution_id = 1
link_build_payload = '{"buildId": ' + build_id + '}'
link_build_request = new RequestObject()
link_build_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(link_build_payload)))
link_build_request.setRestUrl("https://testops.katalon.io/api/v1/run-configurations/" + execution_id + "/link-build")
link_build_request.setRestRequestMethod("POST")
addAuthHeader(link_build_request)
addContentTypeHeader(link_build_request)
link_build_response = WSBuiltInKeywords.sendRequest(link_build_request)
status_code = link_build_response.getStatusCode()

// Step 4: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(link_build_response, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
