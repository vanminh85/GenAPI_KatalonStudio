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

// Step 1: Create a new Team
team_request = new RequestObject()
team_request.setRestUrl("https://testops.katalon.io/api/v1/teams")
team_request.setRestRequestMethod("POST")
addAuthHeader(team_request)
addContentTypeHeader(team_request)
team_payload = '{"name": "Test Team", "role": "OWNER"}'
team_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(team_payload)))
team_response = WSBuiltInKeywords.sendRequest(team_request)
WSBuiltInKeywords.verifyResponseStatusCode(team_response, 200)

team_data = new JsonSlurper().parseText(team_response.getResponseText())
team_id = team_data["id"]

// Step 2: Create a new Project under the Team
project_request = new RequestObject()
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_payload = '{"name": "Test Project", "teamId": ' + team_id + '}'
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

project_data = new JsonSlurper().parseText(project_response.getResponseText())
project_id = project_data["id"]

// Step 3: Create a new Test Project under the Project
test_project_request = new RequestObject()
test_project_request.setRestUrl("https://testops.katalon.io/api/v1/test-projects/upload-script-repo")
test_project_request.setRestRequestMethod("POST")
addAuthHeader(test_project_request)
addContentTypeHeader(test_project_request)
test_project_payload = '{"name": "Test Test Project", "projectId": ' + project_id + ', "batch": "batch", "folderPath": "folderPath", "fileName": "fileName", "uploadedPath": "uploadedPath"}'
test_project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(test_project_payload)))
test_project_response = WSBuiltInKeywords.sendRequest(test_project_request)
WSBuiltInKeywords.verifyResponseStatusCode(test_project_response, 200)

test_project_data = new JsonSlurper().parseText(test_project_response.getResponseText())

// Step 4: Create a new Test Case Resource
test_case_request = new RequestObject()
test_case_request.setRestUrl("https://testops.katalon.io/api/v1/test-cases/update")
test_case_request.setRestRequestMethod("POST")
addAuthHeader(test_case_request)
addContentTypeHeader(test_case_request)
test_case_payload = '{"name": "Test Case", "path": "testPath", "previousStatus": "PASSED", "alias": "Test Alias", "testModuleId": 123, "webUrl": "http://test-url.com", "description": "Test Description", "project": ' + JsonOutput.toJson(project_data) + ', "testProject": ' + JsonOutput.toJson(test_project_data) + ', "type": "TEST_CASE", "testType": "G4_TEST_CASE", "urlId": "testId"}'
test_case_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(test_case_payload)))
test_case_response = WSBuiltInKeywords.sendRequest(test_case_request)
WSBuiltInKeywords.verifyResponseStatusCode(test_case_response, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
