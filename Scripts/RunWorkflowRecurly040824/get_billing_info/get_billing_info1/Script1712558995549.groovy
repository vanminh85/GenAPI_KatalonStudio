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

// Step 1: Create a new account
account_payload = '{"code": "test_account__unique__", "first_name": "John", "last_name": "Doe", "email": "john.doe@example.com"}'
account_request = new RequestObject()
account_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(account_payload)))
account_request.setRestUrl("https://v3.recurly.com/accounts")
account_request.setRestRequestMethod("POST")
addAuthHeader(account_request)
addContentTypeHeader(account_request)
account_response = WSBuiltInKeywords.sendRequest(account_request)
WSBuiltInKeywords.verifyResponseStatusCode(account_response, 201)
account_id = new JsonSlurper().parseText(account_response.getResponseText())['id']

// Step 2: Create a new billing info
billing_info_payload = '{"first_name": "John", "last_name": "Doe", "address": {"phone": "1234567890", "street1": "123 Main St", "city": "New York", "region": "NY", "postal_code": "10001", "country": "US"}, "payment_method": {"object": "credit_card", "card_type": "Visa", "first_six": "123456", "last_four": "7890", "exp_month": 12, "exp_year": 2023}, "primary_payment_method": true, "backup_payment_method": false}'
billing_info_request = new RequestObject()
billing_info_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(billing_info_payload)))
billing_info_request.setRestUrl("https://v3.recurly.com/accounts/" + account_id + "/billing_infos")
billing_info_request.setRestRequestMethod("POST")
addAuthHeader(billing_info_request)
addContentTypeHeader(billing_info_request)
billing_info_response = WSBuiltInKeywords.sendRequest(billing_info_request)
WSBuiltInKeywords.verifyResponseStatusCode(billing_info_response, 201)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}