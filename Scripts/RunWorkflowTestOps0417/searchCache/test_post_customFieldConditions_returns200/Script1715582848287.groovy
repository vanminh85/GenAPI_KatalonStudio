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

// Step 1: Create a CustomFieldCondition
custom_field_condition_payload = '{"field": "field__unique__", "value": "value__unique__"}'
request1 = new RequestObject()
request1.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(custom_field_condition_payload)))
request1.setRestUrl("https://testops.katalon.io/api/v1/custom-field-conditions")
request1.setRestRequestMethod("POST")
addAuthHeader(request1)
addContentTypeHeader(request1)
response1 = WSBuiltInKeywords.sendRequest(request1)
WSBuiltInKeywords.verifyResponseStatusCode(response1, 201)

// Step 2: POST /api/v1/search/cache with a valid SearchRequest containing the created CustomFieldCondition
search_request_payload = '''
{
    "searchRequestConditions": [
        {
            "field": "field__unique__",
            "value": "value__unique__"
        }
    ],
    "customFieldConditions": [
        {
            "field": "field__unique__",
            "value": "value__unique__"
        }
    ],
    "searchRequestPagination": {
        "page": 0,
        "size": 10,
        "sort": ["field"]
    },
    "searchRequestGroupBys": [],
    "searchRequestFunctions": [],
    "type": "type__unique__",
    "searchEntity": "Execution",
    "timeZone": "UTC"
}
'''
request2 = new RequestObject()
request2.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(search_request_payload)))
request2.setRestUrl("https://testops.katalon.io/api/v1/search/cache")
request2.setRestRequestMethod("POST")
addAuthHeader(request2)
addContentTypeHeader(request2)
response2 = WSBuiltInKeywords.sendRequest(request2)
WSBuiltInKeywords.verifyResponseStatusCode(response2, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
