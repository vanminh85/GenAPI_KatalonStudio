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

// Step 1: Create a Git Repository for the Test Project
gitRepoPayload = '{"name": "TestProjectRepo__unique__", "repository": "https://github.com/testproject", "branch": "main", "vcsType": "GITHUB"}'

gitRequest = new RequestObject()
gitRequest.setBodyContent(new HttpTextBodyContent(gitRepoPayload))
gitRequest.setRestUrl("https://testops.katalon.io/api/v1/git/create")
gitRequest.setRestRequestMethod("POST")

addAuthHeader(gitRequest)
addContentTypeHeader(gitRequest)

gitResponse = WSBuiltInKeywords.sendRequest(gitRequest)
WSBuiltInKeywords.verifyResponseStatusCode(gitResponse, 200)
