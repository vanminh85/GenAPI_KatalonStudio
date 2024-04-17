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

// Step 1: Create a new Baseline Collection Group
def baselineCollectionGroupRequest = new RequestObject()
baselineCollectionGroupRequest.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/1")
baselineCollectionGroupRequest.setRestRequestMethod("POST")
addAuthHeader(baselineCollectionGroupRequest)
addContentTypeHeader(baselineCollectionGroupRequest)

def baselineCollectionGroupPayload = '{"id": 1, "name": "BaselineCollectionGroup__unique__", "latestVersion": {"id": 1, "version": 1, "baselines": [], "numberOfBaselines": 0, "updatedBy": {"id": 1}, "baselineCollectionGroup": {"id": 1}, "unsaved": false, "defaultMethod": "PIXEL", "draftDefaultMethod": "PIXEL", "threshold": 0, "draftThreshold": 0, "createAt": "2022-01-01T00:00:00Z"}, "runConfigurations": [], "updatedAt": "2022-01-01T00:00:00Z", "order": 1, "lastRun": {"id": 1}}'
baselineCollectionGroupRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(baselineCollectionGroupPayload)))

def baselineCollectionGroupResponse = WSBuiltInKeywords.sendRequest(baselineCollectionGroupRequest)
WSBuiltInKeywords.verifyResponseStatusCode(baselineCollectionGroupResponse, 200)

// Step 2: Update Baseline Collection Group with server error
def updatedBaselineCollectionGroupRequest = new RequestObject()
updatedBaselineCollectionGroupRequest.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/1")
updatedBaselineCollectionGroupRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedBaselineCollectionGroupRequest)
addContentTypeHeader(updatedBaselineCollectionGroupRequest)

def updatedBaselineCollectionGroupPayload = '{"id": 1, "name": "UpdatedBaselineCollectionGroup__unique__", "latestVersion": {"id": 1, "version": 1, "baselines": [], "numberOfBaselines": 0, "updatedBy": {"id": 1}, "baselineCollectionGroup": {"id": 1}, "unsaved": false, "defaultMethod": "PIXEL", "draftDefaultMethod": "PIXEL", "threshold": 0, "draftThreshold": 0, "createAt": "2022-01-01T00:00:00Z"}, "runConfigurations": [], "updatedAt": "2022-01-01T00:00:00Z", "order": 1, "lastRun": {"id": 1}}'
updatedBaselineCollectionGroupRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedBaselineCollectionGroupPayload)))

def updatedBaselineCollectionGroupResponse = WSBuiltInKeywords.sendRequest(updatedBaselineCollectionGroupRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedBaselineCollectionGroupResponse, 500)

// Step 3: Verify response status code is 500
if (updatedBaselineCollectionGroupResponse.getStatusCode() == 500) {
    println("Step 3 - Response status code is 500 - Test Passed")
} else {
    println("Step 3 - Response status code is not 500 - Test Failed")
}

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}


