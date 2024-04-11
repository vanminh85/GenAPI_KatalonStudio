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
        authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
        request.getHttpHeaderProperties().add(authHeader)
    }
}

def addContentTypeHeader(request) {
    contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
    request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a new Organization
orgRequest = new RequestObject()
orgRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)
orgPayload = '{"name": "Organization__unique__", "role": "OWNER", "domain": "example.com"}'
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

// Step 2: Create a new Team
teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)
teamPayload = '{"name": "Team__unique__", "role": "OWNER", "organizationId": ' + orgResponse.getResponseText() + '.id}'
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 3: Create a new GitRepository
gitRepoRequest = new RequestObject()
gitRepoRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRepoRequest.setRestRequestMethod("POST")
addAuthHeader(gitRepoRequest)
addContentTypeHeader(gitRepoRequest)
gitRepoPayload = '{"testProjectId": 1, "name": "GitRepo__unique__", "repository": "https://github.com/example/repo", "branch": "main"}'
gitRepoRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(gitRepoPayload)))
gitRepoResponse = WSBuiltInKeywords.sendRequest(gitRepoRequest)
WSBuiltInKeywords.verifyResponseStatusCode(gitRepoResponse, 200)

// Step 4: Create a new TestProject
testProjectRequest = new RequestObject()
testProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + teamResponse.getResponseText() + '.id')
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)
testProjectPayload = '{"name": "TestProject__unique__", "description": "Test project description", "defaultTestProject": true, "uploadFileId": 1, "projectId": 1, "teamId": ' + teamResponse.getResponseText() + '.id, "type": "GIT", "gitRepository": {"id": ' + gitRepoResponse.getResponseText() + '.id, "name": "GitRepo__unique__", "repository": "https://github.com/example/repo", "branch": "main"}}'
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

// Step 5: Update the TestProject
updatedTestProjectRequest = new RequestObject()
updatedTestProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + testProjectResponse.getResponseText() + '.id')
updatedTestProjectRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedTestProjectRequest)
addContentTypeHeader(updatedTestProjectRequest)
updatedTestProjectPayload = '{"name": "UpdatedTestProject__unique__", "description": "Updated test project description", "defaultTestProject": false, "uploadFileId": 2, "projectId": 2, "teamId": ' + testProjectResponse.getResponseText() + '.teamId, "type": "KS", "gitRepository": {"id": ' + testProjectResponse.getResponseText() + '.gitRepository.id, "name": "UpdatedGitRepo__unique__", "repository": "https://github.com/example/updated-repo", "branch": "dev"}}'
updatedTestProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedTestProjectPayload)))
updatedTestProjectResponse = WSBuiltInKeywords.sendRequest(updatedTestProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedTestProjectResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
