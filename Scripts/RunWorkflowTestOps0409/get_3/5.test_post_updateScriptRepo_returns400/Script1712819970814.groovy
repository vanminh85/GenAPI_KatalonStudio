import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
    authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
    if (authToken) {
        authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
        request.getHttpHeaderProperties().add(authHeader)
    }
}

def addContentTypeHeader(request) {
    contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
    request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a Test Project
testProjectPayload = '{"name": "TestProjectToUpdateScriptRepo", "description": "Test Project to Update Script Repo", "defaultTestProject": false, "type": "GIT", "gitRepository": {"name": "TestProjectToUpdateScriptRepo"}}'
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)

// Step 2: Update the Test Project script repository
testProjectId = new JsonSlurper().parseText(response1.getResponseText())['id']
updateScriptRepoPayload = '{"batch": "batch__unique__", "folderPath": "folderPath__unique__", "fileName": "fileName__unique__", "uploadedPath": "uploadedPath__unique__"}'
request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateScriptRepoPayload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + testProjectId + "/update-script-repo")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)

// Step 3: Verify the response status code is 400
assert WSBuiltInKeywords.verifyResponseStatusCode(response2, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
