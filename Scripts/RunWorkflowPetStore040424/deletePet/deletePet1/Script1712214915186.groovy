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

// Step 1: Create a new Category
category_payload = '{"id": 1, "name": "Test Category"}'
category_request = new RequestObject()
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(category_payload)))
category_request.setRestUrl("https://petstore.swagger.io/v2/category")
category_request.setRestRequestMethod("POST")
addAuthHeader(category_request)
addContentTypeHeader(category_request)
category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

// Step 2: Create a new Pet with the created Category
pet_payload = '{"name": "Test Pet", "photoUrls": ["url1", "url2"], "category": {"id": 1, "name": "Test Category"}}'
pet_request = new RequestObject()
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(pet_payload)))
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)
pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)
pet_id = new JsonSlurper().parseText(pet_response.getResponseText())['id']

// Step 3: Update the created Pet
update_payload = '{"name": "Updated Test Pet", "status": "available"}'
update_request = new RequestObject()
update_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(update_payload)))
update_request.setRestUrl("https://petstore.swagger.io/v2/pet/${pet_id}")
update_request.setRestRequestMethod("POST")
addAuthHeader(update_request)
addContentTypeHeader(update_request)
update_response = WSBuiltInKeywords.sendRequest(update_request)
WSBuiltInKeywords.verifyResponseStatusCode(update_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
