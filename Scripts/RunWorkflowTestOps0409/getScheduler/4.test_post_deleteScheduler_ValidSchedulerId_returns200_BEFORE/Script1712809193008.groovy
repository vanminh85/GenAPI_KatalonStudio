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

def createSchedulerRequest = new RequestObject()
createSchedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/{id}/schedulers")
createSchedulerRequest.setRestRequestMethod("POST")
addAuthHeader(createSchedulerRequest)
addContentTypeHeader(createSchedulerRequest)

def payload = '{"name": "Test Scheduler__unique__", "startTime": "2022-01-01T00:00:00", "nextTime": "2022-01-01T01:00:00", "endTime": "2022-01-01T02:00:00", "active": true, "interval": 60, "intervalUnit": "MINUTE", "runConfigurationId": 12345, "exceededLimitTime": false}'
createSchedulerRequest.setBodyContent(new HttpTextBodyContent(payload))

def createSchedulerResponse = WSBuiltInKeywords.sendRequest(createSchedulerRequest)
def scheduler_id = new JsonSlurper().parseText(createSchedulerResponse.getResponseText())["id"]

def deleteSchedulerRequest = new RequestObject()
deleteSchedulerRequest.setRestUrl("https://testops.katalon.io/api/v1/test-projects/{id}/schedulers/${scheduler_id}")
deleteSchedulerRequest.setRestRequestMethod("DELETE")
addAuthHeader(deleteSchedulerRequest)

def deleteSchedulerResponse = WSBuiltInKeywords.sendRequest(deleteSchedulerRequest)
WSBuiltInKeywords.verifyResponseStatusCode(deleteSchedulerResponse, 200)
