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
requestStep1 = new RequestObject()
addAuthHeader(requestStep1)
addContentTypeHeader(requestStep1)
payloadStep1 = '{"id": 3, "name": "Pet Without Category", "photoUrls": ["url1"], "status": "available"}'
requestStep1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payloadStep1)))
requestStep1.setRestUrl("https://petstore.swagger.io/v2/pet")
requestStep1.setRestRequestMethod("POST")
responseStep1 = WSBuiltInKeywords.sendRequest(requestStep1)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep1, 200)

// Step 2
requestStep2 = new RequestObject()
addAuthHeader(requestStep2)
addContentTypeHeader(requestStep2)
payloadStep2 = '{"name": "Pet Without Category", "status": "sold"}'
requestStep2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payloadStep2)))
requestStep2.setRestUrl("https://petstore.swagger.io/v2/pet/3")
requestStep2.setRestRequestMethod("POST")
responseStep2 = WSBuiltInKeywords.sendRequest(requestStep2)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep2, 405)

// Step 3
println(responseStep2.getStatusCode())

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
