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

// Step 1: Create a new Scheduler
def createSchedulerRequest = new RequestObject()
createSchedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/123/schedulers")
createSchedulerRequest.setRestRequestMethod("POST")
addAuthHeader(createSchedulerRequest)
addContentTypeHeader(createSchedulerRequest)

def createSchedulerPayload = '{"name": "TestScheduler__unique__", "startTime": "2022-01-01T00:00:00Z", "nextTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T00:00:00Z", "active": true, "interval": 60, "intervalUnit": "MINUTE", "runConfigurationId": 12345, "exceededLimitTime": false}'
createSchedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createSchedulerPayload)))

def createSchedulerResponse = WSBuiltInKeywords.sendRequest(createSchedulerRequest)
def createSchedulerId = new JsonSlurper().parseText(createSchedulerResponse.getResponseText())["id"]

// Step 3: Send a PUT request to update the Scheduler
def updateSchedulerRequest = new RequestObject()
updateSchedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/123/schedulers/${createSchedulerId}")
updateSchedulerRequest.setRestRequestMethod("PUT")
addAuthHeader(updateSchedulerRequest)
addContentTypeHeader(updateSchedulerRequest)

def updateSchedulerPayload = '{"name": "UpdatedTestScheduler__unique__", "startTime": "2022-01-01T01:00:00Z", "nextTime": "2022-01-01T01:00:00Z", "endTime": "2022-01-01T01:00:00Z", "active": false, "interval": 30, "intervalUnit": "MINUTE", "runConfigurationId": 54321, "exceededLimitTime": true}'
updateSchedulerRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateSchedulerPayload)))

def updateSchedulerResponse = WSBuiltInKeywords.sendRequest(updateSchedulerRequest)

// Step 4: Verify the response status code is 200
def isStatusCode200 = WSBuiltInKeywords.verifyResponseStatusCode(updateSchedulerResponse, 200)
if (isStatusCode200) {
	println("Test passed: Status code is 200")
} else {
	println("Test failed: Status code is not 200")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}