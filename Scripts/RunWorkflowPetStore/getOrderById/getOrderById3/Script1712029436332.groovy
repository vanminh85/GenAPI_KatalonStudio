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

def order_payload = [
	"petId": 0,
	"quantity": 1,
	"shipDate": "2022-01-01T00:00:00Z",
	"status": "placed",
	"complete": true
]

def createOrderRequest = new RequestObject()
createOrderRequest.setBodyContent(new HttpTextBodyContent(replaceSuffixWithUUID(JsonOutput.toJson(order_payload))))
createOrderRequest.setRestUrl("https://petstore.swagger.io/v2/store/order")
createOrderRequest.setRestRequestMethod("POST")
addAuthHeader(createOrderRequest)
addContentTypeHeader(createOrderRequest)

def createOrderResponse = WSBuiltInKeywords.sendRequest(createOrderRequest)
WSBuiltInKeywords.verifyResponseStatusCode(createOrderResponse, 200)

def order_id = new JsonSlurper().parseText(createOrderResponse.getResponseText())["id"]

def updateOrderRequest = new RequestObject()
updateOrderRequest.setRestUrl("https://petstore.swagger.io/v2/store/order/${order_id}")
updateOrderRequest.setRestRequestMethod("POST")
addAuthHeader(updateOrderRequest)
addContentTypeHeader(updateOrderRequest)

def updateOrderResponse = WSBuiltInKeywords.sendRequest(updateOrderRequest)
WSBuiltInKeywords.verifyResponseStatusCode(updateOrderResponse, 400)

WSBuiltInKeywords.verifyResponseStatusCode(updateOrderResponse, 400)

def replaceSuffixWithUUID(payload) {
	replacedString = payload.replaceAll('unique__', uuid)
	return replacedString
}
