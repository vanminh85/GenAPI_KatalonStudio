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

// Step 1: Create a new Pet
def pet_payload = '''
{
    "name": "Test Pet",
    "photoUrls": [
        "http://test.com/image1.jpg",
        "http://test.com/image2.jpg"
    ]
}
'''

def pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(pet_payload)))
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)

def pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

def pet_id = (new JsonSlurper()).parseText(pet_response.getResponseText())["id"]

// Step 2: Create a new Order
def order_payload = '''
{
    "id": 1,
    "petId": ''' + pet_id + ''',
	"quantity": 1,
	"shipDate": "2022-01-01T00:00:00Z",
	"status": "placed",
	"complete": true
}
'''

def order_request = new RequestObject()
order_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(order_payload)))
order_request.setRestUrl("https://petstore.swagger.io/v2/store/order")
order_request.setRestRequestMethod("POST")
addAuthHeader(order_request)
addContentTypeHeader(order_request)

def order_response = WSBuiltInKeywords.sendRequest(order_request)
WSBuiltInKeywords.verifyResponseStatusCode(order_response, 200)

def order_id = (new JsonSlurper()).parseText(order_response.getResponseText())["id"]

// Step 3: Send a POST request to '/store/order/{orderId}'
def update_order_request = new RequestObject()
update_order_request.setRestUrl("https://petstore.swagger.io/v2/store/order/" + order_id)
update_order_request.setRestRequestMethod("POST")
addAuthHeader(update_order_request)

def update_order_response = WSBuiltInKeywords.sendRequest(update_order_request)
WSBuiltInKeywords.verifyResponseStatusCode(update_order_response, 400)

// Step 4: Verify that the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(update_order_response, 400)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
