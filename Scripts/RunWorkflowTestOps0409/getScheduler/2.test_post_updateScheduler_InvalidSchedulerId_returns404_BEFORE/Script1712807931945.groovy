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
def schedulerPayload = '{"name": "Test Scheduler", "startTime": "2022-01-01T00:00:00Z", "nextTime": "2022-01-01T00:00:00Z", "endTime": "2022-01-01T00:00:00Z", "active": true, "interval": 60, "intervalUnit": "MINUTE", "runConfigurationId": 12345, "exceededLimitTime": false}'
def createSchedulerRequest = new RequestObject()
createSchedulerRequest.setBodyContent(new HttpTextBodyContent(schedulerPayload))
createSchedulerRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/123/schedulers")
createSchedulerRequest.setRestRequestMethod("POST")
addAuthHeader(createSchedulerRequest)
addContentTypeHeader(createSchedulerRequest)
def createSchedulerResponse = WSBuiltInKeywords.sendRequest(createSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createSchedulerResponse, 200)

// Step 2: Use an invalid 'schedulerId'
def invalidSchedulerId = 999999

// Step 3: Send a PUT request with updated data
def updatedSchedulerPayload = '{"name": "Updated Test Scheduler"}'
def updateSchedulerRequest = new RequestObject()
updateSchedulerRequest.setBodyContent(new HttpTextBodyContent(updatedSchedulerPayload))
updateSchedulerRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/123/schedulers/${invalidSchedulerId}")
updateSchedulerRequest.setRestRequestMethod("PUT")
addAuthHeader(updateSchedulerRequest)
addContentTypeHeader(updateSchedulerRequest)
def updateSchedulerResponse = WSBuiltInKeywords.sendRequest(updateSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateSchedulerResponse, 404)