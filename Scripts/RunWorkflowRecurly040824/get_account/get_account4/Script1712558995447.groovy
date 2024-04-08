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

// Step 1: Create a new account using the createAccount_ValidData_201 test case
def createAccountRequest = new RequestObject()
createAccountRequest.setRestUrl("https://v3.recurly.com/accounts")
createAccountRequest.setRestRequestMethod("POST")
addAuthHeader(createAccountRequest)
addContentTypeHeader(createAccountRequest)

def createAccountPayload = '{"code": "test_account__unique__", "acquisition": {"channel": "Online Store"}}'
createAccountRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(createAccountPayload)))

def createAccountResponse = WSBuiltInKeywords.sendRequest(createAccountRequest)
def createAccountData = new JsonSlurper().parseText(createAccountResponse.getResponseText())
def accountId = createAccountData.get("id")

// Step 3: Create necessary data for the account update request
def updateData = '{"username": "test_username__unique__", "email": "test_email__unique__@example.com", "preferred_locale": "en-US", "preferred_time_zone": "America/Los_Angeles"}'

// Step 4: Send a PUT request to /accounts/{account_id} with the created data
def updateRequest = new RequestObject()
updateRequest.setRestUrl("https://v3.recurly.com/accounts/" + accountId)
updateRequest.setRestRequestMethod("PUT")
addAuthHeader(updateRequest)
addContentTypeHeader(updateRequest)
updateRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(updateData)))

def updateResponse = WSBuiltInKeywords.sendRequest(updateRequest)

// Step 5: Verify that the response status code is 200
def isUpdateSuccessful = WSBuiltInKeywords.verifyResponseStatusCode(updateResponse, 200)
if (isUpdateSuccessful) {
	println("Test Passed: Response status code is 200")
} else {
	println("Test Failed: Response status code is not 200")
}

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
