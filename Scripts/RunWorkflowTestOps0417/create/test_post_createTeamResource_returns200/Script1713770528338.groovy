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

def organizationPayload = '{"name": "Organization__unique__", "role": "OWNER", "domain": "example.com", "subdomainUrl": "example", "tier": "PROFESSIONAL", "requestedUserVerified": true}'
def teamPayload = '{"name": "Team__unique__", "role": "OWNER", "organizationId": 1}'

def request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(organizationPayload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/organizations/{id}/trial-request")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
def response1 = WSBuiltInKeywords.sendRequest(request1)

def request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/teams")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
def response2 = WSBuiltInKeywords.sendRequest(request2)

def request3 = new RequestObject()
request3.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
request3.setRestUrl("https://testops.katalon.io/api/v1/teams")
request3.setRestRequestMethod("POST")
addAuthHeader(request3)
addContentTypeHeader(request3)
def response3 = WSBuiltInKeywords.sendRequest(request3)

def verification = WSBuiltInKeywords.verifyResponseStatusCode(response3, 200)
if (verification) {
	println("Step 4 - Verification: Response status code is 200 - PASSED")
} else {
	println("Step 4 - Verification: Response status code is not 200 - FAILED")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

