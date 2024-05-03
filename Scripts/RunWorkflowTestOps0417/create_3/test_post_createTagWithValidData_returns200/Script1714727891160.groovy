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

def orgTrialRequest = new RequestObject()
orgTrialRequest.setRestUrl("https://testops.katalon.io/api/v1/organizations/1/trial-request")
orgTrialRequest.setRestRequestMethod("POST")

def orgTrialRequestPayload = '{"organization": {"id": 1}, "userRequest": {"id": 1}, "status": "PENDING", "formRequest": "formRequest__unique__", "feature": "KSE"}'
orgTrialRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgTrialRequestPayload)))

addAuthHeader(orgTrialRequest)
addContentTypeHeader(orgTrialRequest)

def response = WSBuiltInKeywords.sendRequest(orgTrialRequest)

WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
