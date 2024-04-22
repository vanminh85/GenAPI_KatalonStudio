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
		authHeader = new TestObjectProperty("authorization", ConditionType.EQUALS, authToken)
		request.getHttpHeaderProperties().add(authHeader)
	}
}

def addContentTypeHeader(request) {
	contentTypeHeader = new TestObjectProperty("content-type", ConditionType.EQUALS, "application/json")
	request.getHttpHeaderProperties().add(contentTypeHeader)
}

uuid = UUID.randomUUID().toString()

// Step 1: Create a SystemLabel resource
systemLabelPayload = '{"entityType": "EXECUTION_TEST_RESULT"}'
systemLabelRequest = new RequestObject()
systemLabelRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(systemLabelPayload)))
systemLabelRequest.setRestUrl("https://testops.katalon.io/api/v1/labels/system-labels")
systemLabelRequest.setRestRequestMethod("POST")
addAuthHeader(systemLabelRequest)
addContentTypeHeader(systemLabelRequest)
systemLabelResponse = WSBuiltInKeywords.sendRequest(systemLabelRequest)
WSBuiltInKeywords.verifyResponseStatusCode(systemLabelResponse, 200)

systemLabelId = new JsonSlurper().parseText(systemLabelResponse.getResponseText())[0].id

// Step 3: Create a Label resource with systemLabel property referencing the created SystemLabel resource
labelPayload = '{"name": "Label__unique__", "entityType": "EXECUTION_TEST_RESULT", "systemLabel": {"id": systemLabelId}}'
labelRequest = new RequestObject()
labelRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(labelPayload)))
labelRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/${systemLabelId}/label")
labelRequest.setRestRequestMethod("POST")
addAuthHeader(labelRequest)
addContentTypeHeader(labelRequest)
labelResponse = WSBuiltInKeywords.sendRequest(labelRequest)
WSBuiltInKeywords.verifyResponseStatusCode(labelResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

