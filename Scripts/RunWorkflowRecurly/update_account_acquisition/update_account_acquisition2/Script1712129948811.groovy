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

def base_url = "https://v3.recurly.com"

// Step 1: Create a new AccountAcquisitionUpdate resource
def acquisition_update_payload = '''
{
    "cost": {
        "currency": "USD",
        "amount": 100
    },
    "channel": "advertising",
    "subchannel": "online",
    "campaign": "summer_sale"
}
'''

def acquisition_update_request = new RequestObject()
acquisition_update_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(acquisition_update_payload)))
acquisition_update_request.setRestUrl(base_url + "/account_acquisition_updates")
acquisition_update_request.setRestRequestMethod("POST")
addAuthHeader(acquisition_update_request)
addContentTypeHeader(acquisition_update_request)

def acquisition_update_response = WSBuiltInKeywords.sendRequest(acquisition_update_request)
WSBuiltInKeywords.verifyResponseStatusCode(acquisition_update_response, 201)

def acquisition_update_id = (new JsonSlurper()).parseText(acquisition_update_response.getResponseText())["id"]

// Step 2: Create a new AccountAcquisition resource
def acquisition_payload = '''
{
    "id": "<generated_account_id>",
    "object": "account_acquisition",
    "account": "<existing_account_data>",
    "created_at": "<current_date_time>",
    "updated_at": "<current_date_time>",
    "campaign": "''' + acquisition_update_id + '''"
}
'''

def acquisition_request = new RequestObject()
acquisition_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(acquisition_payload)))
acquisition_request.setRestUrl(base_url + "/account_acquisitions")
acquisition_request.setRestRequestMethod("POST")
addAuthHeader(acquisition_request)
addContentTypeHeader(acquisition_request)

def acquisition_response = WSBuiltInKeywords.sendRequest(acquisition_request)
WSBuiltInKeywords.verifyResponseStatusCode(acquisition_response, 201)

def account_id = (new JsonSlurper()).parseText(acquisition_response.getResponseText())["id"]

// Step 3: Call the API POST /accounts/{account_id}/acquisition
def post_url = base_url + "/accounts/" + account_id + "/acquisition"
def post_request = new RequestObject()
post_request.setRestUrl(post_url)
post_request.setRestRequestMethod("POST")
addAuthHeader(post_request)
addContentTypeHeader(post_request)

def post_response = WSBuiltInKeywords.sendRequest(post_request)
WSBuiltInKeywords.verifyResponseStatusCode(post_response, 400)

// Step 4: Verify the response status code is 400
WSBuiltInKeywords.verifyResponseStatusCode(post_response, 400)

println("Test case executed successfully.")

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}