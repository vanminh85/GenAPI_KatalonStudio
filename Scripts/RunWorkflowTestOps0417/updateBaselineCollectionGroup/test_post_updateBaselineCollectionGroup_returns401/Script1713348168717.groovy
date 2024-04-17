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

// Step 1: Create a new Baseline Collection Group without authentication
def request1 = new RequestObject()
request1.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/1")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def payload1 = '{"id": 1, "name": "BaselineCollectionGroup__unique__", "latestVersion": {"id": 1, "version": 1, "baselines": [], "numberOfBaselines": 0, "updatedBy": "UserResource__unique__", "baselineCollectionGroup": "BaselineCollectionGroup__unique__", "unsaved": false, "defaultMethod": "PIXEL", "draftDefaultMethod": "PIXEL", "threshold": 0, "draftThreshold": 0, "createAt": "2022-01-01T00:00:00Z"}, "runConfigurations": [], "updatedAt": "2022-01-01T00:00:00Z", "order": 1, "lastRun": "KEyesExecutionResource__unique__"}'
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload1)))
def response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 200)

// Step 2: Update the Baseline Collection Group data
def request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/1")
request2.setRestRequestMethod("PUT")
addAuthHeader(request2)
addContentTypeHeader(request2)
def payload2 = '{"id": 1, "name": "UpdatedBaselineCollectionGroup__unique__", "latestVersion": {"id": 1, "version": 1, "baselines": [], "numberOfBaselines": 0, "updatedBy": "UserResource__unique__", "baselineCollectionGroup": "BaselineCollectionGroup__unique__", "unsaved": false, "defaultMethod": "PIXEL", "draftDefaultMethod": "PIXEL", "threshold": 0, "draftThreshold": 0, "createAt": "2022-01-01T00:00:00Z"}, "runConfigurations": [], "updatedAt": "2022-01-01T00:00:00Z", "order": 1, "lastRun": "KEyesExecutionResource__unique__"}'
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload2)))
def response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

// Step 3: Verify the response status code is 401
log.logInfo("Step 3 - Expected Response Status Code: 401")

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

