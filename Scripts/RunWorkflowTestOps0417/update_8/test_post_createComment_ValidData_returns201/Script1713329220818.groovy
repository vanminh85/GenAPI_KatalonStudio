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

// Step 1: Create a new Team
def teamRequest = new RequestObject()
teamRequest.setRestUrl("https://testops.katalon.io/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)

def teamPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    "name": "Team__unique__",
    "role": "OWNER",
    "organizationId": 1
])))
teamRequest.setBodyContent(teamPayload)

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

def teamId = new JsonSlurper().parseText(teamResponse.getResponseText())["id"]

// Step 2: Extract the teamId
// Already extracted in Step 1

// Step 3: Create a new Project
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)

def projectPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    "name": "Project__unique__",
    "teamId": teamId,
    "timezone": "UTC",
    "status": "ACTIVE",
    "canAutoIntegrate": true,
    "sampleProject": false
])))
projectRequest.setBodyContent(projectPayload)

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def projectId = new JsonSlurper().parseText(projectResponse.getResponseText())["id"]

// Step 4: Extract the projectId
// Already extracted in Step 3

// Step 5: Create a new Comment
def commentRequest = new RequestObject()
commentRequest.setRestUrl("https://testops.katalon.io/api/v1/comments")
commentRequest.setRestRequestMethod("POST")
addAuthHeader(commentRequest)
addContentTypeHeader(commentRequest)

def commentPayload = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
    "objectId": 1,
    "projectId": projectId,
    "content": "This is a test comment",
    "teamId": teamId,
    "objectType": "TEST_CASE",
    "email": "test@example.com",
    "displayName": "Test User",
    "displayAvatar": "avatar.jpg",
    "bySystem": false
])))
commentRequest.setBodyContent(commentPayload)

def commentResponse = WSBuiltInKeywords.sendRequest(commentRequest)
WSBuiltInKeywords.verifyResponseStatusCode(commentResponse, 201)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}


