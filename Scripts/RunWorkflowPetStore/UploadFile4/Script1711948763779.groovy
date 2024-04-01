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

def pet_payload = [
	"name": "doggie__unique__",
	"photoUrls": ["photoUrl1", "photoUrl2"]
]

def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)

def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)
def pet_id = (new JsonSlurper()).parseText(pet_response.getResponseText())["id"]

def upload_image_endpoint = "https://petstore.swagger.io/v2/pet/${pet_id}/uploadImage"

def upload_image_request = new RequestObject()
upload_image_request.setRestUrl(upload_image_endpoint)
upload_image_request.setRestRequestMethod("POST")
addAuthHeader(upload_image_request)
addContentTypeHeader(upload_image_request)

def upload_image_response = WSBuiltInKeywords.sendRequest(upload_image_request)
WSBuiltInKeywords.verifyResponseStatusCode(upload_image_response, 400)
assert upload_image_response.getStatusCode() == 400

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

