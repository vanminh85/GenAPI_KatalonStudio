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
def categoryRequest = new RequestObject()
categoryRequest.setRestUrl("https://petstore.swagger.io/v2/pet")
categoryRequest.setRestRequestMethod("POST")
addAuthHeader(categoryRequest)
addContentTypeHeader(categoryRequest)
def categoryData = '{"name": "TestCategory__unique__"}'
categoryRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(categoryData)))
def categoryResponse = WSBuiltInKeywords.sendRequest(categoryRequest)
WSBuiltInKeywords.verifyResponseStatusCode(categoryResponse, 200)

// Step 2: Create a new Pet
def petRequest = new RequestObject()
petRequest.setRestUrl("https://petstore.swagger.io/v2/pet")
petRequest.setRestRequestMethod("POST")
addAuthHeader(petRequest)
addContentTypeHeader(petRequest)
def petData = '{"name": "TestPet__unique__", "photoUrls": ["http://test.com/image.jpg"], "category": {"name": "TestCategory__unique__"}}'
petRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(petData)))
def petResponse = WSBuiltInKeywords.sendRequest(petRequest)
WSBuiltInKeywords.verifyResponseStatusCode(petResponse, 200)
def petId = new JsonSlurper().parseText(petResponse.getResponseText())['id']

// Step 3: Create a new Order
def orderRequest = new RequestObject()
orderRequest.setRestUrl("https://petstore.swagger.io/v2/store/order")
orderRequest.setRestRequestMethod("POST")
addAuthHeader(orderRequest)
addContentTypeHeader(orderRequest)
def orderData = '{"petId": ' + petId + ', "quantity": 1, "shipDate": "2022-01-01T00:00:00Z", "status": "placed", "complete": true}'
orderRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orderData)))
def orderResponse = WSBuiltInKeywords.sendRequest(orderRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orderResponse, 200)

// Step 4: Upload an image for the pet with an invalid pet ID
def invalidPetId = "invalid_pet_id"
def imageRequest = new RequestObject()
imageRequest.setRestUrl("https://petstore.swagger.io/v2/pet/" + invalidPetId + "/uploadImage")
imageRequest.setRestRequestMethod("POST")
addAuthHeader(imageRequest)
addContentTypeHeader(imageRequest)
def imageData = '{"image": "image_data"}'
imageRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(imageData)))
def imageResponse = WSBuiltInKeywords.sendRequest(imageRequest)
WSBuiltInKeywords.verifyResponseStatusCode(imageResponse, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
