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

def request1 = new RequestObject()
request1.setRestUrl("https://testops.katalon.io/api/v1/organizations")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TestOrg", "role": "OWNER"}')))

def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

def organization_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

def request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/projects")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TestProject", "teamId": ' + organization_id + '}')))

def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def project_id = new JsonSlurper().parseText(response2.getResponseText())["id"]

def request3 = new RequestObject()
request3.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TestTestProject", "projectId": ' + project_id + '}')))

def response3 = WSBuiltInKeywords.sendRequest(request3)
WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)

def test_project_id = new JsonSlurper().parseText(response3.getResponseText())["id"]

def request4 = new RequestObject()
request4.setRestUrl("https://testops.katalon.io/api/v1/test-folder/sub-folders")
request4.setRestRequestMethod("POST")
addAuthHeader(request4)
addContentTypeHeader(request4)
request4.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TestFolder", "testProject": ' + test_project_id + '}')))

def response4 = WSBuiltInKeywords.sendRequest(request4)
WSBuiltInKeywords.verifyResponseStatusCode(response4, 200)

def test_folder_id = new JsonSlurper().parseText(response4.getResponseText())["id"]

def request5 = new RequestObject()
request5.setRestUrl("https://testops.katalon.io/api/v1/tags")
request5.setRestRequestMethod("POST")
addAuthHeader(request5)
addContentTypeHeader(request5)
request5.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID('{"name": "TestTag", "projectId": ' + project_id + ', "organizationId": ' + organization_id + '}')))

def response5 = WSBuiltInKeywords.sendRequest(request5)
WSBuiltInKeywords.verifyResponseStatusCode(response5, 201)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}