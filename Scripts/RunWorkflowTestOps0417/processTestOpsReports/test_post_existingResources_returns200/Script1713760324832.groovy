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

def existing_resources = [
	"FileResource": ["id": 1, "uploadFileId": 1, "self": "self_url", "name": "file_name", "path": "file_path", "url": "file_url", "hash": "file_hash", "size": 100, "uploadUrl": "upload_url", "signedUrl": "signed_url", "thumbnailUrl": "thumbnail_url"],
	"UploadFileResource": ["path": "upload_path", "fileName": "upload_file_name", "type": "TSC_EXECUTION", "base64Content": "base64_content", "fileHandleId": 1, "thumbnailId": 1],
	"UploadBatchResource": ["id": 1, "done": true, "webUrl": "web_url"],
	"UploadBatchFileResource": ["folderPath": "folder_path", "end": true, "fileName": "batch_file_name", "uploadedPath": "uploaded_path", "fullPath": "full_path", "sessionId": "session_id"]
]

def payload = [
	"FileResource": existing_resources["FileResource"],
	"UploadFileResource": existing_resources["UploadFileResource"],
	"UploadBatchResource": existing_resources["UploadBatchResource"],
	"UploadBatchFileResource": existing_resources["UploadBatchFileResource"]
]

def request = new RequestObject()
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(payload))))
request.setRestUrl("https://testops.katalon.io/api/v1/testops-reports")
request.setRestRequestMethod("POST")
addAuthHeader(request)
addContentTypeHeader(request)

def response = WSBuiltInKeywords.sendRequest(request)
assert WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

