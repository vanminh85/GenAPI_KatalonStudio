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

// Step 1: Create a new Baseline Collection Group with missing required fields
def postRequest = new RequestObject()
postRequest.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/{id}")
postRequest.setRestRequestMethod("POST")
addAuthHeader(postRequest)
addContentTypeHeader(postRequest)

def postPayload = '{"id": "1__unique__", "name": "Baseline Collection Group Name__unique__"}'
postRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(postPayload)))

def postResponse = WSBuiltInKeywords.sendRequest(postRequest)
println("Step 1 - POST Response Status Code: " + postResponse.getStatusCode())

// Step 2: Update the Baseline Collection Group data
def putRequest = new RequestObject()
putRequest.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/" + postPayload["id"])
putRequest.setRestRequestMethod("PUT")
addAuthHeader(putRequest)
addContentTypeHeader(putRequest)

def putPayload = '{"id": "' + postPayload["id"] + '", "name": "Updated Baseline Collection Group Name__unique__"}'
putRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(putPayload)))

def putResponse = WSBuiltInKeywords.sendRequest(putRequest)
println("Step 2 - PUT Response Status Code: " + putResponse.getStatusCode())

// Step 3: Verify the response status code is 400
def statusCode = putResponse.getStatusCode()
println("Step 3 - Verification Status Code: " + statusCode)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
