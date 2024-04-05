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
category_request = new RequestObject()
category_request.setRestUrl("https://petstore.swagger.io/v2/category")
category_request.setRestRequestMethod("POST")
addAuthHeader(category_request)
addContentTypeHeader(category_request)
category_payload = '{"id": 4, "name": "Fourth Category"}'
category_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(category_payload)))
category_response = WSBuiltInKeywords.sendRequest(category_request)
WSBuiltInKeywords.verifyResponseStatusCode(category_response, 200)

// Step 2: Create a new Pet
pet_request = new RequestObject()
pet_request.setRestUrl("https://petstore.swagger.io/v2/pet")
pet_request.setRestRequestMethod("POST")
addAuthHeader(pet_request)
addContentTypeHeader(pet_request)
pet_payload = '{"id": 4, "category": {"id": 4, "name": "Fourth Category"}, "name": "Fourth Pet", "photoUrls": ["url7", "url8"]}'
pet_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(pet_payload)))
pet_response = WSBuiltInKeywords.sendRequest(pet_request)
WSBuiltInKeywords.verifyResponseStatusCode(pet_response, 200)

// Step 3: Send a POST request to update the Pet
update_request = new RequestObject()
update_request.setRestUrl("https://petstore.swagger.io/v2/pet/4")
update_request.setRestRequestMethod("POST")
addAuthHeader(update_request)
addContentTypeHeader(update_request)
update_payload = '{"name": "Updated Fourth Pet Name"}'
update_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(update_payload)))
update_response = WSBuiltInKeywords.sendRequest(update_request)
WSBuiltInKeywords.verifyResponseStatusCode(update_response, 405)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
