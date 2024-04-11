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

// Step 1: Create a Test Project
createTestProjectRequest = new RequestObject()
createTestProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
createTestProjectRequest.setRestRequestMethod("POST")
addAuthHeader(createTestProjectRequest)
addContentTypeHeader(createTestProjectRequest)

createTestProjectPayload = '{"name": "TestProjectToGet", "description": "Test Project to Get", "defaultTestProject": false, "type": "GIT", "gitRepository": {"name": "TestProjectToGetRepo"}}'
createTestProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createTestProjectPayload)))

createTestProjectResponse = WSBuiltInKeywords.sendRequest(createTestProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createTestProjectResponse, 200)

testProjectId = new JsonSlurper().parseText(createTestProjectResponse.getResponseText())['id']

// Step 2: Retrieve the Test Project
getTestProjectRequest = new RequestObject()
getTestProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + testProjectId)
getTestProjectRequest.setRestRequestMethod("GET")
addAuthHeader(getTestProjectRequest)

getTestProjectResponse = WSBuiltInKeywords.sendRequest(getTestProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(getTestProjectResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
