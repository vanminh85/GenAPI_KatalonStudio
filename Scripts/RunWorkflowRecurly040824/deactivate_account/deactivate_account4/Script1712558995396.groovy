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

// Step 1: Create a new account
def createAccountRequest = new RequestObject()
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)
def createAccountPayload = '{"code": "test_account__unique__"}'
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))
def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
def account_id = new JsonSlurper().parseText(createAccountResponse.getResponseText())["id"]

// Step 2: Extract the account_id
// Account ID is already extracted in Step 1

// Step 3: Create necessary data for updating the account
def updated_data = [
    "username": "updated_username__unique__",
    "email": "updated_email@example.com",
    "first_name": "Updated",
    "last_name": "User"
]

// Step 4: Send a PUT request to update the account
def updateAccountRequest = new RequestObject()
def update_url = "https://v3.recurly.com/accounts/${account_id}"
updateAccountRequest.setRestUrl(update_url)
updateAccountRequest.setRestRequestMethod("PUT")
addAuthHeader(updateAccountRequest)
addContentTypeHeader(updateAccountRequest)
updateAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(updated_data))))
def updateAccountResponse = WSBuiltInKeywords.sendRequest(updateAccountRequest)

// Step 5: Verify the response status code
WSBuiltInKeywords.verifyResponseStatusCode(updateAccountResponse, 200)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
