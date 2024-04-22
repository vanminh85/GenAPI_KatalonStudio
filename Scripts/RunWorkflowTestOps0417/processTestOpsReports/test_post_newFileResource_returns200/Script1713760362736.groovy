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

def fileResourcePayload = '''
{
    "uploadFileId": 123,
    "self": "http://example.com/file",
    "name": "test_file",
    "path": "/files/test_file",
    "url": "http://example.com/files/test_file",
    "hash": "abc123",
    "size": 1024,
    "uploadUrl": "http://example.com/upload",
    "signedUrl": "http://example.com/signed",
    "thumbnailUrl": "http://example.com/thumbnail"
}
'''

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(fileResourcePayload)))
request.setRestUrl("https://testops.katalon.io/api/v1/testops-reports")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

