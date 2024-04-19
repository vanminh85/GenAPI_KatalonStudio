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

def request1 = new RequestObject()
request1.setRestUrl("https://testops.katalon.io/api/v1/organizations/123")
request1.setRestRequestMethod("GET")
addAuthHeader(request1)
WSBuiltInKeywords.sendRequest(request1)

def request2 = new RequestObject()
request2.setRestUrl("https://testops.katalon.io/api/v1/teams/456")
request2.setRestRequestMethod("GET")
addAuthHeader(request2)
WSBuiltInKeywords.sendRequest(request2)

def request3 = new RequestObject()
request3.setRestUrl("https://testops.katalon.io/api/v1/projects/789")
request3.setRestRequestMethod("GET")
addAuthHeader(request3)
WSBuiltInKeywords.sendRequest(request3)

def request4 = new RequestObject()
request4.setRestUrl("https://testops.katalon.io/api/v1/configs/1011")
request4.setRestRequestMethod("GET")
addAuthHeader(request4)
WSBuiltInKeywords.sendRequest(request4)

def payload = JsonOutput.toJson([
	id: 0,
	email: "test@example.com",
	firstName: "John",
	lastName: "Doe",
	password: "password123",
	invitingUrl: "https://example.com",
	avatar: "avatar.jpg",
	configs: ["$ref": "https://testops.katalon.io/api/v1/configs/1011"],
	projects: [["$ref": "https://testops.katalon.io/api/v1/projects/789"]],
	teams: [["$ref": "https://testops.katalon.io/api/v1/teams/456"]],
	organizations: [["$ref": "https://testops.katalon.io/api/v1/organizations/123"]],
	organizationFeature: [],
	trialExpirationDate: "2022-12-31T23:59:59Z",
	systemRole: "USER",
	surveyStatus: "NOT_SUBMITTED",
	sessionTimeout: 3600,
	businessUser: false,
	canCreateOfflineKSE: false,
	canCreateOfflineRE: false,
	samlSSO: false,
	createdAt: "2022-01-01T00:00:00Z",
	verified: false,
	fullName: "John Doe"
])

def request5 = new RequestObject()
request5.setRestUrl("https://testops.katalon.io/api/v1/users")
request5.setRestRequestMethod("POST")
addAuthHeader(request5)
addContentTypeHeader(request5)
request5.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))
WSBuiltInKeywords.sendRequest(request5)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
