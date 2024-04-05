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

def userPayload = '''
{
    "id": 1,
    "username": "test_user__unique__",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "phone": "1234567890",
    "userStatus": "invalid_status"
}
'''

def createWithListRequest = new RequestObject()
createWithListRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
createWithListRequest.setRestUrl("https://petstore.swagger.io/v2/user/createWithList")
createWithListRequest.setRestRequestMethod("POST")
addAuthHeader(createWithListRequest)
addContentTypeHeader(createWithListRequest)

def createWithListResponse = WSBuiltInKeywords.sendRequest(createWithListRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createWithListResponse, 200)

def createUserRequest = new RequestObject()
createUserRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(userPayload)))
createUserRequest.setRestUrl("https://petstore.swagger.io/v2/user/" + new JsonSlurper().parseText(userPayload).username)
createUserRequest.setRestRequestMethod("POST")
addAuthHeader(createUserRequest)
addContentTypeHeader(createUserRequest)

def createUserResponse = WSBuiltInKeywords.sendRequest(createUserRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createUserResponse, 400)

println(createUserResponse.getResponseText().getStatusCode())

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
