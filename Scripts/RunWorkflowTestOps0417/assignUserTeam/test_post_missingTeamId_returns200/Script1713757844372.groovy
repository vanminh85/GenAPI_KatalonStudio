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

// Step 1: Create a new User
user_payload = '{"firstName": "John__unique__", "lastName": "Doe__unique__"}'
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(user_payload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/users")
request1.setRestRequestMethod("POST")
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
addAuthHeader(request1)

user_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 2: Extract the userId
new_user_ids = [user_id]

// Step 3: Add the new User to a Team
add_user_payload = '{"teamId": 123, "newUserIds": new_user_ids}'
request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(add_user_payload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/users/add")
request2.setRestRequestMethod("POST")
addContentTypeHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)
addAuthHeader(request2)

// Step 4: Verify the response status code
assert WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}