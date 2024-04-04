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

// Step 1: Create a new Pet
def addPetRequest = findTestObject('addPet')
def pet_payload = '{"name": "pet__unique__", "photoUrls": ["url1", "url2"], "category": {"id": 1, "name": "category__unique__"}}'
addPetRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(pet_payload)))
addContentTypeHeader(addPetRequest)
WS.sendRequest(addPetRequest)

// Step 2: Retrieve the Pet ID from the response
def pet_id = new JsonSlurper().parseText(WS.sendRequest(findTestObject('addPet')).getResponseText())['id']

// Step 3: Get the Pet by ID using the retrieved Pet ID
def getPetRequest = findTestObject('getPetById', ['petId': pet_id])
addAuthHeader(getPetRequest)
WS.sendRequest(getPetRequest)

def replaceSuffixWithUUID(payload) {
    replacedString = payload.replaceAll('unique__', uuid)
    return replacedString
}
