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

// Step 1: Create necessary data for the dependent resources
projectId = 1__unique__
externalRelease = "release1__unique__"
releaseStatistics = ["release": ["startTime": "2022-01-01", "endTime": "2022-01-02", "projectId": projectId, "externalRelease": externalRelease, "releaseStatistics": [:], "releaseStatus": "READY"], "totalPassed": 10, "totalFailed": 5, "totalExecution": 15, "totalDefect": 2, "totalDuration": 3600]
releaseStatus = "READY"

// Step 2: Send a POST request to /api/v1/releases with valid data for the ReleaseResource
def request = new RequestObject()
request.setRestUrl("https://testops.katalon.io/api/v1/releases")
request.setRestRequestMethod("POST")

def payload = ["id": 1__unique__, "name": "Release 1", "startTime": "2022-01-01", "endTime": "2022-01-02", "description": "Test Release", "projectId": projectId, "closed": false, "createdAt": "2022-01-01T00:00:00Z", "externalRelease": ["id": 1__unique__, "name": externalRelease], "releaseStatistics": releaseStatistics, "builds": [], "releaseStatus": releaseStatus]
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(payload))))

addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)

// Step 3: Verify that the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
