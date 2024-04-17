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

// Step 1: Create a new Build
build_payload = '{"projectId": 1, "project": {"id": 1, "name": "Project__unique__"}, "releaseId": 1, "release": {"id": 1, "name": "Release__unique__"}, "buildStatistics": {"id": 1, "totalExecution": 0, "totalPassed": 0, "totalFailed": 0}, "name": "Build__unique__", "description": "Build Description", "date": "2022-01-01T00:00:00Z"}'

request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(build_payload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/build")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)

response1 = WSBuiltInKeywords.sendRequest(request1)
build_id = new JsonSlurper().parseText(response1.getResponseText())["id"]

// Step 2: Delete the newly created Build
request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/build/" + build_id)
request2.setRestRequestMethod("DELETE")
addAuthHeader(request2)

response2 = WSBuiltInKeywords.sendRequest(request2)

// Step 3: Verify the response status code is 200
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

