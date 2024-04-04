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

// Step 1: Create a new Category
def request1 = new RequestObject()
request1.setRestUrl("https://petstore.swagger.io/v2/category")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def bodyContent1 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["id": 5, "name": "Test Category 5"])))
request1.setBodyContent(bodyContent1)
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Create a new Pet
def request2 = new RequestObject()
request2.setRestUrl("https://petstore.swagger.io/v2/pet")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def bodyContent2 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "Test Pet 5", "photoUrls": ["url1", "url2"], "category": ["id": 5, "name": "Test Category 5"])))
request2.setBodyContent(bodyContent2)
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)
def pet_id = new JsonSlurper().parseText(response2.getResponseText())["id"]

// Step 3: Update the created Pet with missing required field
def request3 = new RequestObject()
request3.setRestUrl("https://petstore.swagger.io/v2/pet/${pet_id}")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
def bodyContent3 = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["photoUrls": ["url1", "url2"]])))
request3.setBodyContent(bodyContent3)
def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 405)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
