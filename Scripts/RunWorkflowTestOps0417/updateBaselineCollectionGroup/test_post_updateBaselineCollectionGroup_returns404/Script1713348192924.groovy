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

def payload = '''
{
    "id": 9999,
    "name": "Updated Baseline Collection Group Name__unique__",
    "latestVersion": {
        "id": 1,
        "version": 1,
        "baselines": [],
        "numberOfBaselines": 0,
        "updatedBy": {"id": 1, "name": "User__unique__"},
        "baselineCollectionGroup": {"id": 1, "name": "Baseline Collection Group__unique__"},
        "unsaved": false,
        "defaultMethod": "PIXEL",
        "draftDefaultMethod": "PIXEL",
        "threshold": 10,
        "draftThreshold": 10,
        "createAt": "2022-01-01T00:00:00Z"
    },
    "runConfigurations": [],
    "updatedAt": "2022-01-01T00:00:00Z",
    "order": 1,
    "lastRun": {"id": 1, "name": "KEyes Execution__unique__"}
}
'''

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(payload)))
request.setRestUrl("https://testops.katalon.io/api/v1/baseline-collection-groups/9999")
request.setRestRequestMethod("PUT")
addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 404)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

