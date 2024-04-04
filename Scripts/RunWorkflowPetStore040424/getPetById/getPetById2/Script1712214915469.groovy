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
request_category = new RequestObject()
request_category.setRestUrl("https://petstore.swagger.io/v2/category")
request_category.setRestRequestMethod("POST")
addAuthHeader(request_category)
addContentTypeHeader(request_category)
payload_category = '{"id": 2, "name": "Test Category 2"}'
request_category.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_category)))
response_category = WSBuiltInKeywords.sendRequest(request_category)
WSBuiltInKeywords.verifyResponseStatusCode(response_category, 200)

// Step 2
request_pet = new RequestObject()
request_pet.setRestUrl("https://petstore.swagger.io/v2/pet")
request_pet.setRestRequestMethod("POST")
addAuthHeader(request_pet)
addContentTypeHeader(request_pet)
payload_pet = '{"name": "Test Pet 2", "category": {"id": 2, "name": "Test Category 2"}}'
request_pet.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload_pet)))
response_pet = WSBuiltInKeywords.sendRequest(request_pet)
WSBuiltInKeywords.verifyResponseStatusCode(response_pet, 405)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
