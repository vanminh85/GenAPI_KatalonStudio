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

// Step 1: Create a new Build
def createBuildRequest = new RequestObject()
createBuildRequest.setRestUrl("https://testops.katalon.io/api/v1/build")
createBuildRequest.setRestRequestMethod("POST")
addAuthHeader(createBuildRequest)
addContentTypeHeader(createBuildRequest)

def createBuildPayload = JsonOutput.toJson([
    "projectId": 1,
    "name": "BuildName__unique__",
    "description": "BuildDescription__unique__"
])
createBuildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createBuildPayload)))

def createBuildResponse = WSBuiltInKeywords.sendRequest(createBuildRequest)
def createdBuildId = new JsonSlurper().parseText(createBuildResponse.getResponseText())["id"]

// Step 2: Extract the 'id' of the created Build
def runConfigurationsLinkBuildEndpoint = "/api/v1/run-configurations/${createdBuildId}/link-build"

// Step 3: Execute the POST request to link a Run Configuration to the created Build with a non-existent 'id'
def nonExistentId = 9999
def linkBuildRequest = new RequestObject()
linkBuildRequest.setRestUrl("https://testops.katalon.io" + runConfigurationsLinkBuildEndpoint)
linkBuildRequest.setRestRequestMethod("POST")
addAuthHeader(linkBuildRequest)
addContentTypeHeader(linkBuildRequest)

def linkBuildPayload = JsonOutput.toJson([
    "buildId": createdBuildId
])
linkBuildRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(linkBuildPayload)))

def linkBuildResponse = WSBuiltInKeywords.sendRequest(linkBuildRequest)

// Step 4: Verify that the response status code is 404
def isStatusCode404 = WSBuiltInKeywords.verifyResponseStatusCode(linkBuildResponse, 404)
assert isStatusCode404

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
