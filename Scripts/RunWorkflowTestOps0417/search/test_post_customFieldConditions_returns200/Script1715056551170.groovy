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

// Request 1: Create a new Search Request with customFieldConditions
def searchRequest = new RequestObject()
def searchRequestPayload = '{"searchRequestConditions": [], "customFieldConditions": ["customFieldCondition__unique__"], "searchRequestPagination": {}, "searchRequestGroupBys": [], "searchRequestFunctions": [], "type": "Execution", "searchEntity": "Execution", "timeZone": "UTC"}'
searchRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(searchRequestPayload)))
searchRequest.setRestUrl("https://testops.katalon.io/api/v1/search")
searchRequest.setRestRequestMethod("POST")
addAuthHeader(searchRequest)
addContentTypeHeader(searchRequest)

// Send the request and verify the response status code
def searchResponse = WSBuiltInKeywords.sendRequest(searchRequest)
WSBuiltInKeywords.verifyResponseStatusCode(searchResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}