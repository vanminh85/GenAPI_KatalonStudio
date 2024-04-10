import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

//Test Steps:
//"1. Create a new Pet with Name: 'Test Pet', PhotoUrls: ['url1', 'url2'], Category ID: 1, Status: 'available' using POST /pet API",
//"2. Send a GET request to /pet/findByStatus?status=available",
//"3. Verify the response status code is 200"

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



// Step 1: Create a new Pet
def petRequest = new RequestObject()
petRequest.setRestUrl("https://petstore.swagger.io/v2/pet")
petRequest.setRestRequestMethod("POST")
addAuthHeader(petRequest)
addContentTypeHeader(petRequest)
def petPayload = '{"id": 1, "category": {"id": 1, "name": "Test Category"}, "name": "Test Pet", "photoUrls": ["url1", "url2"], "tags": [], "status": "available"}'
petRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(petPayload)))
def petResponse = WSBuiltInKeywords.sendRequest(petRequest)
WSBuiltInKeywords.verifyResponseStatusCode(petResponse, 200)

// Step 2: Send a GET request to /pet/findByStatus?status=available
def findByStatusRequest = new RequestObject()
findByStatusRequest.setRestUrl("https://petstore.swagger.io/v2/pet/findByStatus?status=available")
findByStatusRequest.setRestRequestMethod("GET")
addAuthHeader(findByStatusRequest)
def findByStatusResponse = WSBuiltInKeywords.sendRequest(findByStatusRequest)
WSBuiltInKeywords.verifyResponseStatusCode(findByStatusResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}