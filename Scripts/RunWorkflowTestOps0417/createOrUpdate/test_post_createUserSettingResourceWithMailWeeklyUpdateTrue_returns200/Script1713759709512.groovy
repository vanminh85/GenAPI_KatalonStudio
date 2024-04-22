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

def userSettingRequest = new RequestObject()
userSettingRequest.setRestUrl("https://testops.katalon.io/api/v1/user-settings")
userSettingRequest.setRestRequestMethod("POST")
addAuthHeader(userSettingRequest)
addContentTypeHeader(userSettingRequest)

def user_setting_payload = [
	"mailJobStart": false,
	"mailJobEnd": false,
	"mailExecution": false,
	"mailExecutionStatus": "default",
	"mailWeeklyUpdate": true
]

def bodyContent = new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(user_setting_payload)))
userSettingRequest.setBodyContent(bodyContent)

def userSettingResponse = WSBuiltInKeywords.sendRequest(userSettingRequest)
WSBuiltInKeywords.verifyResponseStatusCode(userSettingResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

