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
def categoryData = '{"name": "TestCategory"}'
def requestStep1 = new RequestObject()
requestStep1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(categoryData)))
requestStep1.setRestUrl("https://petstore.swagger.io/v2/pet")
requestStep1.setRestRequestMethod("POST")
addAuthHeader(requestStep1)
addContentTypeHeader(requestStep1)
def responseStep1 = WSBuiltInKeywords.sendRequest(requestStep1)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep1, 200)

// Step 2
def petData = '{"name": "TestPet", "photoUrls": ["http://test.com/image.jpg"], "category": {"name": "TestCategory"}}'
def requestStep2 = new RequestObject()
requestStep2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(petData)))
requestStep2.setRestUrl("https://petstore.swagger.io/v2/pet")
requestStep2.setRestRequestMethod("POST")
addAuthHeader(requestStep2)
addContentTypeHeader(requestStep2)
def responseStep2 = WSBuiltInKeywords.sendRequest(requestStep2)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep2, 200)
def createdPetId = new JsonSlurper().parseText(responseStep2.getResponseText())['id']

// Step 3
def orderData = '{"petId": ' + createdPetId + ', "quantity": 1, "shipDate": "2022-01-01T00:00:00Z", "status": "placed", "complete": true}'
def requestStep3 = new RequestObject()
requestStep3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orderData)))
requestStep3.setRestUrl("https://petstore.swagger.io/v2/store/order")
requestStep3.setRestRequestMethod("POST")
addAuthHeader(requestStep3)
addContentTypeHeader(requestStep3)
def responseStep3 = WSBuiltInKeywords.sendRequest(requestStep3)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep3, 200)

// Step 4
def nonExistingPetId = createdPetId + 1
def imageData = '{"image": "image_data"}'
def requestStep4 = new RequestObject()
requestStep4.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(imageData)))
requestStep4.setRestUrl("https://petstore.swagger.io/v2/pet/" + nonExistingPetId + "/uploadImage")
requestStep4.setRestRequestMethod("POST")
addAuthHeader(requestStep4)
addContentTypeHeader(requestStep4)
def responseStep4 = WSBuiltInKeywords.sendRequest(requestStep4)
WSBuiltInKeywords.verifyResponseStatusCode(responseStep4, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
