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

def categoryRequest = new RequestObject()
categoryRequest.setRestUrl("https://petstore.swagger.io/v2/category")
categoryRequest.setRestRequestMethod("POST")
addAuthHeader(categoryRequest)
addContentTypeHeader(categoryRequest)
def categoryPayload = '{"id": 2, "name": "Another Category"}'
categoryRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(categoryPayload)))
def categoryResponse = WSBuiltInKeywords.sendRequest(categoryRequest)
WSBuiltInKeywords.verifyResponseStatusCode(categoryResponse, 200)

def petRequest = new RequestObject()
petRequest.setRestUrl("https://petstore.swagger.io/v2/pet")
petRequest.setRestRequestMethod("POST")
addAuthHeader(petRequest)
addContentTypeHeader(petRequest)
def petPayload = '{"id": 2, "photoUrls": ["url1"], "category": {"id": 2, "name": "Another Category"}, "name": "pet__unique__", "status": "available"}'
petRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(petPayload)))
def petResponse = WSBuiltInKeywords.sendRequest(petRequest)
WSBuiltInKeywords.verifyResponseStatusCode(petResponse, 200)

def updateRequest = new RequestObject()
updateRequest.setRestUrl("https://petstore.swagger.io/v2/pet/2")
updateRequest.setRestRequestMethod("POST")
addAuthHeader(updateRequest)
addContentTypeHeader(updateRequest)
def updatePayload = '{"status": "sold"}'
updateRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatePayload)))
def updateResponse = WSBuiltInKeywords.sendRequest(updateRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateResponse, 405)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
