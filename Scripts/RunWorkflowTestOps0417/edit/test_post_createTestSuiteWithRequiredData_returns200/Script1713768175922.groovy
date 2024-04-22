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

// Step 1: Create a new Organization
def orgPayload = '''
{
    "name": "Organization__unique__",
    "role": "OWNER",
    "quotaKSE": 100,
    "machineQuotaKSE": 10,
    "quotaEngine": 200,
    "machineQuotaEngine": 20,
    "usedKSE": 50,
    "usedUnlimitedKSE": 0,
    "usedEngine": 100,
    "usedUnlimitedEngine": 0,
    "quotaTestOps": 150,
    "usedTestOps": 50,
    "numberUser": 10,
    "quotaFloatingEngine": 50,
    "usedFloatingEngine": 10,
    "canCreateOfflineKSE": true,
    "canCreateOfflineUnlimitedKSE": false,
    "canCreateOfflineRE": true,
    "canCreateOfflineUnlimitedEngine": false,
    "subscriptionExpiryDateEngine": "2023-01-01T00:00:00Z",
    "subscriptionExpiryDateUnlimitedEngine": "2023-01-01T00:00:00Z",
    "subscriptionExpiryDateKSE": "2023-01-01T00:00:00Z",
    "subscriptionExpiryDateUnlimitedKSE": "2023-01-01T00:00:00Z",
    "subscriptionExpiryDateFloatingEngine": "2023-01-01T00:00:00Z",
    "subscriptionExpiryDateTestOps": "2023-01-01T00:00:00Z",
    "subscribed": true,
    "ksePaygo": true,
    "krePaygo": true,
    "paygoQuota": 200,
    "domain": "example.com",
    "subdomainUrl": "subdomain.example.com",
    "strictDomain": true,
    "logoUrl": "https://example.com/logo.png",
    "samlSSO": true,
    "kreLicense": true,
    "mostRecentProjectAccessedAt": "2022-01-01T00:00:00Z",
    "accountId": 12345,
    "accountUUID": "123e4567-e89b-12d3-a456-426614174000",
    "testOpsFeature": "KSE",
    "platformFeature": "ENGINE",
    "tier": "PROFESSIONAL",
    "requestedUserVerified": true
}
'''

def orgRequest = new RequestObject()
orgRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(orgPayload)))
orgRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/teams")
orgRequest.setRestRequestMethod("POST")
addAuthHeader(orgRequest)
addContentTypeHeader(orgRequest)

def orgResponse = WSBuiltInKeywords.sendRequest(orgRequest)
WSBuiltInKeywords.verifyResponseStatusCode(orgResponse, 200)

// Step 2: Create a new Team
def teamPayload = '''
{
    "name": "Team__unique__",
    "role": "OWNER",
    "users": [],
    "organization": ${orgResponse.getResponseText()},
    "organizationId": ${orgResponse.getResponseText()['id']}
}
'''

def teamRequest = new RequestObject()
teamRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(teamPayload)))
teamRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/teams")
teamRequest.setRestRequestMethod("POST")
addAuthHeader(teamRequest)
addContentTypeHeader(teamRequest)

def teamResponse = WSBuiltInKeywords.sendRequest(teamRequest)
WSBuiltInKeywords.verifyResponseStatusCode(teamResponse, 200)

// Step 3: Create a new Project
def projectPayload = '''
{
    "name": "Project__unique__",
    "teamId": ${teamResponse.getResponseText()['id']},
    "team": ${teamResponse.getResponseText()},
    "timezone": "UTC",
    "status": "ACTIVE",
    "canAutoIntegrate": true,
    "sampleProject": true
}
'''

def projectRequest = new RequestObject()
projectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(projectPayload)))
projectRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/projects")
projectRequest.setRestRequestMethod("POST")
addAuthHeader(projectRequest)
addContentTypeHeader(projectRequest)

def projectResponse = WSBuiltInKeywords.sendRequest(projectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(projectResponse, 200)

// Step 4: Create a new TestProject
def testProjectPayload = '''
{
    "name": "TestProject__unique__",
    "projectId": ${projectResponse.getResponseText()['id']},
    "batch": "batch123",
    "folderPath": "/path/to/folder",
    "fileName": "test_script.py",
    "uploadedPath": "/uploaded/path"
}
'''

def testProjectRequest = new RequestObject()
testProjectRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testProjectPayload)))
testProjectRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-projects/upload-script-repo")
testProjectRequest.setRestRequestMethod("POST")
addAuthHeader(testProjectRequest)
addContentTypeHeader(testProjectRequest)

def testProjectResponse = WSBuiltInKeywords.sendRequest(testProjectRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testProjectResponse, 200)

// Step 5: Create a new Test Suite
def testSuitePayload = '''
{
    "name": "TestSuite__unique__",
    "path": "/path/to/test/suite",
    "testResults": [],
    "project": ${testProjectResponse.getResponseText()},
    "alias": "Alias",
    "lastExecutionTestSuite": {},
    "updatedAt": "2022-01-01T00:00:00Z",
    "testCases": [],
    "testProject": ${testProjectResponse.getResponseText()},
    "createdAt": "2022-01-01T00:00:00Z",
    "user": {},
    "type": "KATALON_STUDIO",
    "testFolder": {},
    "urlId": "test-suite-123"
}
'''

def testSuiteRequest = new RequestObject()
testSuiteRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(testSuitePayload)))
testSuiteRequest.setRestUrl("${GlobalVariable.base_url}/api/v1/test-suites/${testProjectResponse.getResponseText()['id']}/edit")
testSuiteRequest.setRestRequestMethod("POST")
addAuthHeader(testSuiteRequest)
addContentTypeHeader(testSuiteRequest)

def testSuiteResponse = WSBuiltInKeywords.sendRequest(testSuiteRequest)
WSBuiltInKeywords.verifyResponseStatusCode(testSuiteResponse, 200)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}

