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

// Step 1: Create a new CircleCI agent
create_circleci_agent_request = new RequestObject()
create_circleci_agent_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	"name": "CircleCI Agent_${uuid}",
	"url": "https://example.com",
	"username": "circleci_user_${uuid}",
	"token": "circleci_token_${uuid}",
	"project": "my_project",
	"vcsType": "git",
	"branch": "main",
	"teamId": 123,
	"apiKey": "circleci_api_key_${uuid}"
])))
create_circleci_agent_request.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent")
create_circleci_agent_request.setRestRequestMethod("POST")
addAuthHeader(create_circleci_agent_request)
addContentTypeHeader(create_circleci_agent_request)

create_circleci_agent_response = WSBuiltInKeywords.sendRequest(create_circleci_agent_request)
WSBuiltInKeywords.verifyResponseStatusCode(create_circleci_agent_response, 200)

// Step 2: Extract the id of the created CircleCI agent
created_circleci_agent_id = new JsonSlurper().parseText(create_circleci_agent_response.getResponseText())["id"]

// Step 3: Update the CircleCI agent
update_circleci_agent_request = new RequestObject()
update_circleci_agent_request.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson([
	"name": "Updated CircleCI Agent_${uuid}",
	"url": "https://updated-example.com",
	"username": "updated_circleci_user_${uuid}",
	"token": "updated_circleci_token_${uuid}",
	"project": "updated_project",
	"vcsType": "github",
	"branch": "dev",
	"teamId": 456,
	"apiKey": "updated_circleci_api_key_${uuid}"
])))
update_circleci_agent_request.setRestUrl("https://testops.katalon.io/api/v1/circle-ci-agent")
update_circleci_agent_request.setRestRequestMethod("PUT")
addAuthHeader(update_circleci_agent_request)
addContentTypeHeader(update_circleci_agent_request)

update_circleci_agent_response = WSBuiltInKeywords.sendRequest(update_circleci_agent_request)
WSBuiltInKeywords.verifyResponseStatusCode(update_circleci_agent_response, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

