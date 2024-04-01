import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput

import java.util.UUID

import static org.junit.Assert.assertEquals

import groovy.json.JsonSlurper

// Step 1: Create a new Pet resource
def pet_payload = [
	"id": 3,
	"name": "Test Pet",
	"photoUrls": ["http://example.com/image.jpg"],
	"category": [
		"id": 1,
		"name": "Category__unique__"
	]
]

// Step 2: Call the API POST /pet/3
def request = new RequestObject()
request.setRestUrl("https://petstore.swagger.io/v2/pet/3")
request.setRestRequestMethod("POST")
addContentTypeHeader(request)
addAuthHeader(request)
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet_payload))))

def response = WSBuiltInKeywords.sendRequest(request)

// Step 3: Verify that the response status code is 400
assertEquals(400, response.getStatusCode())

println("Test case passed")

def addAuthHeader(request) {
	def authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
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

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
