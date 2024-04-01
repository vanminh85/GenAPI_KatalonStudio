import internal.GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.RequestObject
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

def base_url = "https://petstore.swagger.io/v2"

def pet1_payload = """
{
    "id": 1,
    "category": {
        "id": 1,
        "name": "category1__unique__"
    },
    "name": "pet1__unique__",
    "photoUrls": ["url1"],
    "tags": [
        {
            "id": 1,
            "name": "tag1__unique__"
        }
    ],
    "status": "available"
}
"""

def pet2_payload = """
{
    "id": 2,
    "category": {
        "id": 2,
        "name": "category2__unique__"
    },
    "name": "pet2__unique__",
    "photoUrls": ["url2"],
    "tags": [
        {
            "id": 2,
            "name": "tag2__unique__"
        }
    ],
    "status": "pending"
}
"""

def pet3_payload = """
{
    "id": 3,
    "category": {
        "id": 3,
        "name": "category3__unique__"
    },
    "name": "pet3__unique__",
    "photoUrls": ["url3"],
    "tags": [
        {
            "id": 3,
            "name": "tag3__unique__"
        }
    ],
    "status": "sold"
}
"""

def request = new RequestObject()
request.setRestUrl(base_url + "/pet")
request.setRestRequestMethod("POST")
addContentTypeHeader(request)

def slurper = new JsonSlurper()
def pet1_payload_map = slurper.parseText(pet1_payload)
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet1_payload_map))))
WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def pet2_payload_map = slurper.parseText(pet2_payload)
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet2_payload_map))))
WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def pet3_payload_map = slurper.parseText(pet3_payload)
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(pet3_payload_map))))
WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def status_params = ["available", "pending", "sold"]
request.setRestUrl(base_url + "/pet/findByStatus")
request.setRestRequestMethod("POST")
addContentTypeHeader(request)
def status_params_map = slurper.parseText(JsonOutput.toJson(status_params))
request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(status_params_map))))
WSBuiltInKeywords.sendRequest(request)
WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

WSBuiltInKeywords.verifyResponseStatusCode(response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
