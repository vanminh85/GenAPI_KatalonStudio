import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def addAuthHeader(request) {
	authToken = GlobalVariable.katalon_ai_api_auth_value ?: null
	if (authToken) {
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1
categoryPayload = '{"id": 3, "name": "Third Category"}'
categoryRequest = new RequestObject()
categoryRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(categoryPayload)))
categoryRequest.setRestUrl("https://petstore.swagger.io/v2/category")
categoryRequest.setRestRequestMethod("POST")
addAuthHeader(categoryRequest)
addContentTypeHeader(categoryRequest)
categoryResponse = WSBuiltInKeywords.sendRequest(categoryRequest)
WSBuiltInKeywords.verifyResponseStatusCode(categoryResponse, 200)

// Step 2
petPayload = '{"id": 4, "name": "Pet Without Photos", "category": {"id": 3, "name": "Third Category"}, "status": "available"}'
petRequest = new RequestObject()
petRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(petPayload)))
petRequest.setRestUrl("https://petstore.swagger.io/v2/pet")
petRequest.setRestRequestMethod("POST")
addAuthHeader(petRequest)
addContentTypeHeader(petRequest)
petResponse = WSBuiltInKeywords.sendRequest(petRequest)
WSBuiltInKeywords.verifyResponseStatusCode(petResponse, 200)

// Step 3
updatePayload = '{"name": "Pet Without Photos", "status": "sold"}'
updateRequest = new RequestObject()
updateRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatePayload)))
updateRequest.setRestUrl("https://petstore.swagger.io/v2/pet/4")
updateRequest.setRestRequestMethod("POST")
addAuthHeader(updateRequest)
addContentTypeHeader(updateRequest)
updateResponse = WSBuiltInKeywords.sendRequest(updateRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateResponse, 405)

println("Test case completed.")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
