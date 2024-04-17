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

def tagPayload = '{"name": "Incomplete Tag"}'
def tagRequest = new RequestObject()
tagRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(tagPayload)))
tagRequest.setRestUrl("https://testops.katalon.io/api/v1/tags")
tagRequest.setRestRequestMethod("POST")
addAuthHeader(tagRequest)
addContentTypeHeader(tagRequest)

def tagResponse = WSBuiltInKeywords.sendRequest(tagRequest)
WSBuiltInKeywords.verifyResponseStatusCode(tagResponse, 400)

println("Step 2 - Response Status Code: " + tagResponse.getStatusCode())

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
