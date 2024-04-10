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
request1.setRestUrl("https://testops.katalon.io/api/v1/organizations")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestOrg", "role": "OWNER"]))))
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)
organization_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 2
request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/projects")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestProject", "teamId": organization_id])))
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)
project_id = new JsonSlurper().parseText(response2.getResponseText())["id"]

// Step 3
request3 = new RequestObject()
request3.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestTestProject", "projectId": project_id])))
response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)
test_project_id = new JsonSlurper().parseText(response3.getResponseText())["id"]

// Step 4
request4 = new RequestObject()
request4.setRestUrl("https://testops.katalon.io/api/v1/test-folder/sub-folders")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
request4.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestFolder", "testProject": test_project_id])))
response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 200)
test_folder_id = new JsonSlurper().parseText(response4.getResponseText())["id"]

// Step 5
request5 = new RequestObject()
request5.setRestUrl("https://testops.katalon.io/api/v1/tags")
request5.setRestRequestMethod("POST")
addAuthHeader(request5)
addContentTypeHeader(request5)
request5.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestTag", "projectId": project_id, "organizationId": organization_id])))
response5 = WSBuiltInKeywords.sendRequest(request5)
WSBuiltInKeywords.verifyResponseStatusCode(response5, 201)

// Step 6
request6 = new RequestObject()
request6.setRestUrl("https://testops.katalon.io/api/v1/tags")
request6.setRestRequestMethod("POST")
addAuthHeader(request6)
addContentTypeHeader(request6)
request6.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(["name": "TestTag", "projectId": project_id, "organizationId": organization_id])))
response6 = WSBuiltInKeywords.sendRequest(request6)
WSBuiltInKeywords.verifyResponseStatusCode(response6, 409)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}