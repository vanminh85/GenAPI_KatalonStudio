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

def userSettingsRequest = new RequestObject()
userSettingsRequest.setRestUrl("https://testops.katalon.io/api/v1/user-settings")
userSettingsRequest.setRestRequestMethod("GET")
addAuthHeader(userSettingsRequest)
addContentTypeHeader(userSettingsRequest)

def userSettingsResponse = WSBuiltInKeywords.sendRequest(userSettingsRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userSettingsResponse, 200)

def invalidDataPayload = '{"mailJobStart":"invalid_data","mailJobEnd":"invalid_data","mailExecution":"invalid_data","mailExecutionStatus":"invalid_data","mailWeeklyUpdate":"invalid_data"}'
def updateUserSettingsRequest = new RequestObject()
updateUserSettingsRequest.setRestUrl("https://testops.katalon.io/api/v1/user-settings")
updateUserSettingsRequest.setRestRequestMethod("POST")
addAuthHeader(updateUserSettingsRequest)
addContentTypeHeader(updateUserSettingsRequest)
updateUserSettingsRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(invalidDataPayload)))

def updateUserSettingsResponse = WSBuiltInKeywords.sendRequest(updateUserSettingsRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateUserSettingsResponse, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
