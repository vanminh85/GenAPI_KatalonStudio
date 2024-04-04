import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.UUID

import static org.assertj.core.api.Assertions.assertThat

import com.kms.katalon.core.testobject.ResponseObject

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

def category_payload = [
	"id": 1,
	"name": "Test Category"
]

def category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(category_payload))))
category_request.setRestUrl("https://petstore.swagger.io/v2/category")
category_request.setRestRequestMethod("POST")
addAuthHeader(category_request)
addContentTypeHeader(category_request)

def category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

def pet_payload = [
	"id": 1,
	"category": [
		"id": 1,
		"name": "Test Category"
	],
	"name": "Test Pet",
	"photoUrls": ["https://example.com/photo1.jpg"]
]

def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)

def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 405)

assertThat(pet_response.getStatusCode()).isEqualTo(405)

println("Test case executed successfully.")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

