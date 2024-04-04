import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = "${GlobalVariable.katalon_ai_api_auth_value}" ?: null
	if (authToken) {
		auth_header = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(auth_header)
	}
}

def addContentTypeHeader(request) {
	content_type_header = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(content_type_header)
}

uuid = UUID.randomUUID().toString()

// Step 1
request1 = new RequestObject()
request1.setRestUrl("https://petstore.swagger.io/v2/pet")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
category_data = '{"name": "TestCategory"}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(category_data)))
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

pet_id = new JsonSlurper().parseText(response1.getResponseText())['id']

// Step 2
request2 = new RequestObject()
request2.setRestUrl("https://petstore.swagger.io/v2/pet")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
pet_data = '{"name": "TestPet", "photoUrls": ["http://test.com/image.jpg"], "category": {"name": "TestCategory"}}'
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(pet_data)))
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3
request3 = new RequestObject()
request3.setRestUrl("https://petstore.swagger.io/v2/store/order")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
order_data = '{"petId": ' + pet_id + ', "quantity": 1, "shipDate": "2022-01-01T00:00:00Z", "status": "placed", "complete": true}'
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(order_data)))
response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

// Step 4
request4 = new RequestObject()
request4.setRestUrl("https://petstore.swagger.io/v2/pet/" + pet_id + "/uploadImage")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
image_data = '{"image": "image_data_here"}'
request4.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(image_data)))
response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
