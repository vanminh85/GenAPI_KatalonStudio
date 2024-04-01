import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import internal.GlobalVariable

import groovy.json.JsonOutput

// Step 1: Create a new pet with tags
def payload = [
	"id": 1,
	"category": [
		"id": 1,
		"name": "category__unique__"
	],
	"name": "pet__unique__",
	"photoUrls": ["photoUrl1", "photoUrl2"],
	"tags": [
		[
			"id": 1,
			"name": "tag1__unique__"
		],
		[
			"id": 2,
			"name": "tag2__unique__"
		]
	],
	"status": "available"
]

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(payload))))
request1.setRestUrl("https://petstore.swagger.io/v2/pet")
request1.setRestRequestMethod("POST")
addContentTypeHeader(request1)
addAuthHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Call the API POST /pet/findByTags with invalid tags
def invalid_tags = ["invalid_tag1", "invalid_tag2"]

def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(invalid_tags))))
request2.setRestUrl("https://petstore.swagger.io/v2/pet/findByTags")
request2.setRestRequestMethod("POST")
addContentTypeHeader(request2)
addAuthHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 400)

// Step 3: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(response2, 400)

println("Test case executed successfully.")

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

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

