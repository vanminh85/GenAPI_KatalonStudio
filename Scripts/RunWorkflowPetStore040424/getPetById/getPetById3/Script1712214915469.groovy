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

// Step 1
def requestStep1 = new RequestObject()
requestStep1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"id": 3, "name": "Test Category 3"}')))
requestStep1.setRestUrl("https://petstore.swagger.io/v2/category")
requestStep1.setRestRequestMethod("POST")
addAuthHeader(requestStep1)
addContentTypeHeader(requestStep1)
def responseStep1 = WSBuiltInKeywords.sendRequest(requestStep1)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep1, 200)

// Step 2
def requestStep2 = new RequestObject()
requestStep2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Test Pet 3", "photoUrls": ["url1", "url2"], "category": {"id": 3, "name": "Test Category 3"}}')))
requestStep2.setRestUrl("https://petstore.swagger.io/v2/pet")
requestStep2.setRestRequestMethod("POST")
addAuthHeader(requestStep2)
addContentTypeHeader(requestStep2)
def responseStep2 = WSBuiltInKeywords.sendRequest(requestStep2)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep2, 200)
def pet_id = new JsonSlurper().parseText(responseStep2.getResponseText())['id']

// Step 3
def requestStep3 = new RequestObject()
requestStep3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "Updated Pet Name"}')))
requestStep3.setRestUrl("https://petstore.swagger.io/v2/pet/${pet_id}")
requestStep3.setRestRequestMethod("POST")
addAuthHeader(requestStep3)
addContentTypeHeader(requestStep3)
def responseStep3 = WSBuiltInKeywords.sendRequest(requestStep3)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep3, 405)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
