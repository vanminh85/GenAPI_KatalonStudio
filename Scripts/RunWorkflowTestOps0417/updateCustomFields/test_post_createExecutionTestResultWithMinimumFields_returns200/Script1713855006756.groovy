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

// Step 1: Create a new Project
project_request = new RequestObject()
project_request.setRestUrl("https://testops.katalon.io/api/v1/projects")
project_request.setRestRequestMethod("POST")
addAuthHeader(project_request)
addContentTypeHeader(project_request)
project_payload = '{"name": "Project__unique__", "teamId": 1}'
project_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(project_payload)))
project_response = WSBuiltInKeywords.sendRequest(project_request)
WSBuiltInKeywords.verifyResponseStatusCode(project_response, 200)

// Step 2: Create a new Execution
execution_request = new RequestObject()
execution_request.setRestUrl("https://testops.katalon.io/api/v1/executions")
execution_request.setRestRequestMethod("POST")
addAuthHeader(execution_request)
addContentTypeHeader(execution_request)
execution_payload = '{"projectId": 1}'
execution_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(execution_payload)))
execution_response = WSBuiltInKeywords.sendRequest(execution_request)
WSBuiltInKeywords.verifyResponseStatusCode(execution_response, 200)

// Step 4: Create a new Execution Test Result
execution_test_result_request = new RequestObject()
execution_test_result_request.setRestUrl("https://testops.katalon.io/api/v1/test-results/${id}/custom-fields")
execution_test_result_request.setRestRequestMethod("POST")
addAuthHeader(execution_test_result_request)
addContentTypeHeader(execution_test_result_request)
execution_test_result_payload = '{"testCase": "TestCase__unique__", "execution": "Execution__unique__", "platform": "Platform__unique__", "logId": "LogId__unique__"}'
execution_test_result_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(execution_test_result_payload)))
execution_test_result_response = WSBuiltInKeywords.sendRequest(execution_test_result_request)
WSBuiltInKeywords.verifyResponseStatusCode(execution_test_result_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

