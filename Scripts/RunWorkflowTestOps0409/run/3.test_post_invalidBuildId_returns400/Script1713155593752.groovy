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

// Step 1
executionId = 123
invalidBuildId = 999
payload = '{"buildId": 999}'
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/run-configurations/" + executionId + "/link-build")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
assert WSBuiltInKeywords.verifyResponseStatusCode(response1, 400)

// Step 2
println(response1.getStatusCode())

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
