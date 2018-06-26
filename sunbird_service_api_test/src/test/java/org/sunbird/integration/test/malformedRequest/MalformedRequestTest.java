package org.sunbird.integration.test.malformedRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MalformedRequestTest extends TestNGCitrusTestDesigner {

	@Autowired
	private HttpClient restTestClient;
	@Autowired
	private TestGlobalProperty initGlobalValues;
	private ObjectMapper objectMapper = new ObjectMapper();
	private static String admin_token = null;

	@DataProvider(name = "createRequestDataProvider")
	public Object[][] createRequestDataProvider() {
		String emptyPayLoad = "{\"request\":{}}";
		return new Object[][] {
				new Object[] { emptyPayLoad,
						Constant.RESPONSE_TEMPLATE_LOCATION + "content_type_mandatory_response.json", "createUser",
						"/api/user/v1/create" },
				new Object[] { emptyPayLoad,
						Constant.RESPONSE_TEMPLATE_LOCATION + "content_type_mandatory_response.json", "createOrg",
						"/api/org/v1/create" },
				new Object[] { emptyPayLoad,
						Constant.RESPONSE_TEMPLATE_LOCATION + "content_type_mandatory_response.json", "createCourse",
						"/api/course/v1/create" },
				new Object[] { emptyPayLoad,
						Constant.RESPONSE_TEMPLATE_LOCATION + "content_type_mandatory_response.json", "createPage",
						"/api/page/v1/create" },
				new Object[] { emptyPayLoad,
						Constant.RESPONSE_TEMPLATE_LOCATION + "content_type_mandatory_response.json", "createNote",
						"/api/note/v1/create" } };
	}

	/**
	 * Test for create request without content-type header.
	 *
	 * @param requestJson
	 * @param responseJson
	 * @param testName
	 */
	@Test(dataProvider = "createRequestDataProvider", dependsOnMethods = { "getAdminAuthToken", })
	@CitrusParameters({ "requestJson", "responseJson", "testName", "url" })
	@CitrusTest
	public void createWithoutContentType(String requestJson, String responseJson, String testName, String url) {
		getTestCase().setName(testName);
		System.out.println("request: == " + requestJson);
		http().client(restTestClient).send().post(url)
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
				.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token).payload(requestJson);

		http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST)
				.payload(new ClassPathResource(responseJson));

	}

	/**
	 * Test for create request with invalid(json-ld) content-type header.
	 *
	 * @param requestJson
	 * @param responseJson
	 * @param testName
	 */
	@Test(dataProvider = "createRequestDataProvider", dependsOnMethods = { "getAdminAuthToken", })
	@CitrusParameters({ "requestJson", "responseJson", "testName", "url" })
	@CitrusTest
	public void createWithInvalidContentType(String requestJson, String responseJson, String testName, String url) {
		getTestCase().setName(testName);
		http().client(restTestClient).send().post(url).contentType(Constant.CONTENT_TYPE_APPLICATION_JSON_LD)
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
				.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token).payload(requestJson);

		http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST)
				.payload(new ClassPathResource(responseJson));

	}

	@Test()
	@CitrusTest
	/**
	 * Key cloak admin token generation is required , because on sunbird dev server
	 * after creating user , user have to login first then only his/her account will
	 * be active. so we need to disable that option for created user only. That
	 * option can be disable using keycloak admin auth token. So this method will
	 * generate auth token and that token will be used in
	 * **updateUserRequiredLoginActionTest** method.
	 */
	public void getAdminAuthToken() {
		http().client(restTestClient).send()
				.post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
				.contentType("application/x-www-form-urlencoded")
				.payload("client_id=admin-cli&username=" + initGlobalValues.getKeycloakAdminUser() + "&password="
						+ initGlobalValues.getKeycloakAdminPass() + "&grant_type=password");
		http().client(restTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
					@Override
					public void validate(Map response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.get("access_token"));
						admin_token = (String) response.get("access_token");
					}
				});
	}

}
