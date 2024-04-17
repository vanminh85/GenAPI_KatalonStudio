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

def projectRequest = new RequestObject()
projectRequest.setRestUrl("https://testops.katalon.io/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)
def projectPayload = '{"name": "Project__unique__", "teamId": 123, "timezone": "UTC", "status": "ACTIVE", "canAutoIntegrate": true, "sampleProject": true}'
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

def executionRequest = new RequestObject()
executionRequest.setRestUrl("https://testops.katalon.io/api/v1/executions")
executionRequest.setRestRequestMethod("POST")
addAuthHeader(executionRequest)
addContentTypeHeader(executionRequest)
def executionPayload = '{"status": "PASSED", "startTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T01:00:00Z", "duration": 3600}'
executionRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(executionPayload)))
def executionResponse = WSBuiltInKeywords.sendRequest(executionRequest)
WSBuiltInKeywords.verifyResponseStatusCode(executionResponse, 200)

def baselineRequest = new RequestObject()
baselineRequest.setRestUrl("https://testops.katalon.io/api/v1/baseline-collections")
baselineRequest.setRestRequestMethod("POST")
addAuthHeader(baselineRequest)
addContentTypeHeader(baselineRequest)
def baselinePayload = '{"projectId": 123, "baselineCollectionId": 456, "uploadBatch": "Batch__unique__"}'
baselineRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(baselinePayload)))
def baselineResponse = WSBuiltInKeywords.sendRequest(baselineRequest)
WSBuiltInKeywords.verifyResponseStatusCode(baselineResponse, 200)

def baselineCollectionGroupRequest = new RequestObject()
baselineCollectionGroupRequest.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/${id}")
baselineCollectionGroupRequest.setRestRequestMethod("POST")
addAuthHeader(baselineCollectionGroupRequest)
addContentTypeHeader(baselineCollectionGroupRequest)
def baselineCollectionGroupPayload = '{"name": "BaselineCollectionGroup__unique__", "latestVersion": {"id": 456}}'
baselineCollectionGroupRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(baselineCollectionGroupPayload)))
def baselineCollectionGroupResponse = WSBuiltInKeywords.sendRequest(baselineCollectionGroupRequest)
WSBuiltInKeywords.verifyResponseStatusCode(baselineCollectionGroupResponse, 200)

def updatedBaselineCollectionGroupRequest = new RequestObject()
updatedBaselineCollectionGroupRequest.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/${id}")
updatedBaselineCollectionGroupRequest.setRestRequestMethod("PUT")
addAuthHeader(updatedBaselineCollectionGroupRequest)
addContentTypeHeader(updatedBaselineCollectionGroupRequest)
def updatedBaselineCollectionGroupPayload = '{"name": "UpdatedBaselineCollectionGroup__unique__", "latestVersion": {"id": 456}}'
updatedBaselineCollectionGroupRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updatedBaselineCollectionGroupPayload)))
def updatedBaselineCollectionGroupResponse = WSBuiltInKeywords.sendRequest(updatedBaselineCollectionGroupRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updatedBaselineCollectionGroupResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

