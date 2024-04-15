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

// Step 1: Create a new Project
def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)

def projectPayload = '{"name": "Test Project__unique__", "teamId": 123, "timezone": "UTC", "sampleProject": true}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 2: Update the Project status with invalid data
def invalidProjectRequest = new RequestObject()
invalidProjectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects/update-status")
invalidProjectRequest.setRestRequestMethod("POST")
addAuthHeader(invalidProjectRequest)
addContentTypeHeader(invalidProjectRequest)

def invalidProjectPayload = '{"status": "INVALID_STATUS"}'
invalidProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(invalidProjectPayload)))

def invalidProjectResponse = WSBuiltInKeywords.sendRequest(invalidProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(invalidProjectResponse, 400)

// Step 3: Verify response status code is 400
if (invalidProjectResponse.getStatusCode() == 400) {
    println("Step 3 - Verification: Status Code 400 - PASSED")
} else {
    println("Step 3 - Verification: Status Code not 400 - FAILED")
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
