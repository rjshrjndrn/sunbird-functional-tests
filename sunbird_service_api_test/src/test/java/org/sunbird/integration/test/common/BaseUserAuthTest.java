package org.sunbird.integration.test.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseCode;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.user.EndpointConfig;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseUserAuthTest extends TestNGCitrusTestDesigner {

	@Autowired
	protected HttpClient restTestClient;
	//@Autowired
	protected TestGlobalProperty initGlobalValues = new EndpointConfig().initGlobalValues();

	protected ObjectMapper objectMapper = new ObjectMapper();
	protected static String admin_token = null;
	protected static String userId = null;
	protected static String user_auth_token = null;
	protected static volatile String USER_NAME = "userName";
	String local_auth = "http://localhost:8088";

	@Test()
	@CitrusTest
	public void testCreateUser() {
		String requestJson = createUserMap();
		//getTestCase().setName("createUser");
		http().client(restTestClient).send().post("/v1/user/create").contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey()).payload(requestJson);
		 http().client(restTestClient).receive().response(HttpStatus.OK)
			.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
				@Override
				public void validate(Response response, Map<String, Object> headers, TestContext context) {
					Assert.assertNotNull(response.getId());
					Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
					Assert.assertNotNull(response.getResult().get("response"));
					userId = (String) response.getResult().get("userId");
					Assert.assertNotNull(userId);
				}
			});
		
	}

	@Test()
	@CitrusTest
	/**
	 * Key cloak admin token generation is required for some API to get
	 * authenticated
	 * 
	 */
	public void getAdminAuthToken() {

		http().client(local_auth).send()
				.post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
				.contentType("application/x-www-form-urlencoded")
				.payload("client_id=admin-cli&username=" + initGlobalValues.getKeycloakAdminUser() + "&password="
						+ initGlobalValues.getKeycloakAdminPass() + "&grant_type=password");
		http().client(local_auth).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
					@Override
					public void validate(Map response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.get("access_token"));
						admin_token = (String) response.get("access_token");
					}
				});
	}

	@Test(dependsOnMethods = { "testCreateUser", "getAdminAuthToken" })
	@CitrusTest
	/**
	 * This method will disable user required action change password under keyCloak.
	 * after disabling that , we can generate newly created user auth token.
	 */
	public void updateUserRequiredLoginActionTest() {
		http().client(local_auth).send()
				.put("/auth/admin/realms/" + initGlobalValues.getRelam() + "/users/" + userId)
				.header(Constant.AUTHORIZATION, Constant.BEARER + admin_token)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON).payload("{\"requiredActions\":[]}");
		http().client(local_auth).receive().response(HttpStatus.NO_CONTENT);
	}

	@Test(dependsOnMethods = { "updateUserRequiredLoginActionTest" })
	@CitrusTest
	public void getUserAuthToken() {
		http().client(local_auth).send()
				.post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
				.contentType("application/x-www-form-urlencoded")
				.payload("client_id=" + initGlobalValues.getClientId() + "&username=" + USER_NAME + "@"
						+ initGlobalValues.getSunbirdDefaultChannel() + "&password=password&grant_type=password");
		http().client(local_auth).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
					@Override
					public void validate(Map response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.get("access_token"));
						user_auth_token = (String) response.get("access_token");
					}
				});
	}

	private String createUserMap() {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(Constant.REQUEST, createUserInnerMap());
		try {
			return objectMapper.writeValueAsString(requestMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Map<String, Object> createUserInnerMap() {
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put(Constant.FIRST_NAME, "ft_first_Name");
		innerMap.put(Constant.LAST_NAME, "ft_lastName");
		innerMap.put(Constant.PASSWORD, "password");
		innerMap.put(Constant.EXTERNAL_ID, String.valueOf(System.currentTimeMillis()));
		innerMap.put(Constant.PROVIDER, String.valueOf(System.currentTimeMillis()+10));
		USER_NAME = Constant.USER_NAME_PREFIX + EndpointConfig.val;
		String email = Constant.USER_NAME_PREFIX + EndpointConfig.val + "@gmail.com";
		innerMap.put(Constant.USER_NAME, USER_NAME);
		innerMap.put(Constant.EMAIL, email);
		return innerMap;
	}
}
