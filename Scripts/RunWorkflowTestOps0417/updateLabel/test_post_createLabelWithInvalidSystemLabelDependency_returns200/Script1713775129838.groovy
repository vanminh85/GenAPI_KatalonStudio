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

def systemLabelRequest = new RequestObject()
systemLabelRequest.setRestUrl("https://testops.katalon.io/api/v1/labels/system-labels")
systemLabelRequest.setRestRequestMethod("POST")
addAuthHeader(systemLabelRequest)
addContentTypeHeader(systemLabelRequest)

def systemLabelPayload = '{"entityType": "EXECUTION_TEST_RESULT"}'
systemLabelRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(systemLabelPayload)))

def systemLabelResponse = WSBuiltInKeywords.sendRequest(systemLabelRequest)
WSBuiltInKeywords.verifyResponseStatusCode(systemLabelResponse, 200)

def systemLabelId = new JsonSlurper().parseText(systemLabelResponse.getResponseText())[0]["id"]

def labelRequest = new RequestObject()
labelRequest.setRestUrl("https://testops.katalon.io/api/v1/test-results/${systemLabelId}/label")
labelRequest.setRestRequestMethod("POST")
addAuthHeader(labelRequest)
addContentTypeHeader(labelRequest)

def labelPayload = '''
{
    "name": "Label1__unique__",
    "entityType": "EXECUTION_TEST_RESULT",
    "systemLabel": {"id": ${systemLabelId + 1}},
    "projectId": 1,
    "organizationId": 1,
    "userId": 1,
    "createdAt": "2022-01-01T00:00:00Z",
    "updatedAt": "2022-01-01T00:00:00Z"
}
'''
labelRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(labelPayload)))

def labelResponse = WSBuiltInKeywords.sendRequest(labelRequest)
WSBuiltInKeywords.verifyResponseStatusCode(labelResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

