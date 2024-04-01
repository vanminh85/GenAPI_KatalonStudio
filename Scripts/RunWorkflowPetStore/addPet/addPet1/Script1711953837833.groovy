import internal.GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
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

def category_payload = JsonOutput.toJson([
	"name": "Test Category__unique__"
])

def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(category_payload)))
category_request.setRestUrl("https://petstore.swagger.io/v2/category")
category_request.setRestRequestMethod("POST")
addAuthHeader(category_request)
addContentTypeHeader(category_request)

def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

def category_id = new JsonSlurper().parseText(category_response.getResponseText())["id"]

def pet_payload = JsonOutput.toJson([
	"name": "Test Pet__unique__",
	"photoUrls": ["http://example.com/image.jpg"],
	"category": [
		"id": category_id,
		"name": "Test Category__unique__"
	]
])

def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(pet_payload)))
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)

def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

def pet_id = new JsonSlurper().parseText(pet_response.getResponseText())["id"]

def update_pet_request = new RequestObject()
update_pet_request.setRestUrl("https://petstore.swagger.io/v2/pet/${pet_id}")
update_pet_request.setRestRequestMethod("POST")
addAuthHeader(update_pet_request)
addContentTypeHeader(update_pet_request)

def update_pet_response = WSBuiltInKeywords.sendRequest(update_pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(update_pet_response, 200)

assert update_pet_response.getStatusCode() == 200

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

