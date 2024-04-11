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

// Step 1: Create a Test Project
create_test_project_payload = '{"name": "TestProjectToDelete", "description": "Test Project to Delete", "defaultTestProject": false, "type": "GIT", "gitRepository": {"name": "TestProjectToDeleteRepo"}}'
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(create_test_project_payload))
request1.setRestUrl("https://testops.katalon.io/api/v1/test-projects")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)
test_project_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 2: Delete the Test Project
request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/test-projects/" + test_project_id)
request2.setRestRequestMethod("DELETE")
addAuthHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)
