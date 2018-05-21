/*
 *
 */

package org.sunbird.integration.test.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseCode;
import org.sunbird.common.util.Constants;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder.HttpClientReceiveActionBuilder;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class will have all functional test cases regarding create user, get
 * user details , generate user auth key.
 * 
 * @author Manzarul
 *
 */
public class CreateUserTest extends TestNGCitrusTestDesigner {

	private static String userId = null;
	private static String user_auth_token = null;
	private static String admin_token = null;
	public static Map<String, List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
	private static final String CREATE_USER_URI = "/v1/user/create";
	private static volatile String USER_NAME = "userName";

	/**
	 * User can define the api request and response json structure. first index is
	 * request json object, second is response json object and third is test case
	 * name.
	 * 
	 * @return
	 */
	@DataProvider(name = "createUserDataProvider")
	public Object[][] createUserDataProvider() {
		return new Object[][] {
				new Object[] { Constants.USER_TEMPLATE_LOCATION + "user_first_name_mandatory.json",
						Constants.USER_TEMPLATE_LOCATION + "user_first_name_mandatory_response.json",
						"firstNameMandatoryTest" },
				new Object[] { Constants.USER_TEMPLATE_LOCATION + "user_name_mandatory.json",
						Constants.USER_TEMPLATE_LOCATION + "user_name_mandatory_response.json",
						"UserNameMandatory" },
				new Object[] { Constants.USER_TEMPLATE_LOCATION + "user_invalid_role_type.json",
						Constants.USER_TEMPLATE_LOCATION + "user_invalid_role_type_response.json",
						"invalidRoleType" },
				new Object[] { Constants.USER_TEMPLATE_LOCATION + "user_invalid_language_type.json",
						Constants.USER_TEMPLATE_LOCATION + "user_invalid_language_type_response.json",
						"invalidLanguageType" },
				new Object[] { Constants.USER_TEMPLATE_LOCATION + "user_invalid_dob_format.json",
						Constants.USER_TEMPLATE_LOCATION + "user_invalid_dob_response.json", "invalidDobFormat" } };
	}

	@DataProvider(name = "createUserDynamicDataProvider")
	public Object[][] createUserDynamicJsonData() {
		return new Object[][] { new Object[] { createUserMap(), "usersuccessresponse.json", "createUser" },
				new Object[] { createUserWithDuplicateEmail(),
						Constants.USER_TEMPLATE_LOCATION + "user_duplicate_email_response.json",
						"duplicateEmailTest" },
				new Object[] { createUserWithDuplicateUserName(),
						Constants.USER_TEMPLATE_LOCATION + "user_username_exist_response.json",
						"duplicateUsernameTest" } };
	}

	@Autowired
	private HttpClient restTestClient;
	@Autowired
	private HttpClient localTestClient;

	@Autowired
	private TestGlobalProperty initGlobalValues;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Test(dataProvider = "createUserDynamicDataProvider", priority = 1)
	@CitrusParameters({ "requestJson", "responseJson", "testName" })
	@CitrusTest
	public void testCreateUser(String requestJson, String responseJson, String testName) {
		System.out.println("Create user " + testName + " Data: " + requestJson);
		getTestCase().setName(testName);
		http().client(restTestClient).send().post(CREATE_USER_URI)
				.contentType(Constants.CONTENT_TYPE_APPLICATION_JSON)
				.header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey())
				.payload(requestJson);
		if (!"usersuccessresponse.json".equals(responseJson)) {
			http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST)
					.payload(new ClassPathResource(responseJson));
		} else {
			HttpClientReceiveActionBuilder response = http().client(restTestClient).receive();
			handleUserCreationResponse(response);
		}
	}

	@Test(dataProvider = "createUserDataProvider", priority = 2)
	@CitrusParameters({ "requestJson", "responseJson", "testName" })
	@CitrusTest
	public void testCreateUserFailure(String requestJson, String responseJson, String testName) {
		getTestCase().setName(testName);
		http().client(restTestClient).send().post(CREATE_USER_URI)
				.contentType(Constants.CONTENT_TYPE_APPLICATION_JSON)
				.header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey())
				.payload(new ClassPathResource(requestJson));
		http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST)
				.payload(new ClassPathResource(responseJson));
	}

	/**
	 * This method will handle response for create user.
	 * 
	 * @param response
	 *            HttpClientReceiveActionBuilder
	 */
	private void handleUserCreationResponse(HttpClientReceiveActionBuilder response) {
		response.response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.getId());
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
						Assert.assertNotNull(response.getResult().get("response"));
						userId = (String) response.getResult().get("userId");
						Assert.assertNotNull(userId);
						List<String> list = deletedRecordsMap.get("user");
						if (list == null) {
							list = new ArrayList<>();
						}
						list.add(userId);
						deletedRecordsMap.put("user", list);
					}
				});
	}

	@Test(priority = 3)
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
		http().client(localTestClient).send().post("/realms/master/protocol/openid-connect/token")
				.contentType("application/x-www-form-urlencoded")
				.payload("client_id=admin-cli&username=" + initGlobalValues.getKeycloakAdminUser() + "&password="
						+ initGlobalValues.getKeycloakAdminPass() + "&grant_type=password");
		http().client(localTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
					@Override
					public void validate(Map response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.get("access_token"));
						admin_token = (String) response.get("access_token");
						System.out.println("Admin auth token value ==" + admin_token);
					}
				});
	}

	@Test(priority = 4)
	@CitrusTest
	/**
	 * This method will disable user required action change password under keyCloak.
	 * after disabling that , we can generate newly created user auth token.
	 * after disabling that , we can generate newly created user auth token.
	 */
	public void updateUserRequiredLoginActionTest() {
		http().client(localTestClient).send()
				.put("/admin/realms/" + initGlobalValues.getRelam() + "/users/" + userId)
				.header(Constants.AUTHORIZATION, Constants.BEARER + admin_token)
				.contentType(Constants.CONTENT_TYPE_APPLICATION_JSON).payload("{\"requiredActions\":[]}");
		http().client(localTestClient).receive().response(HttpStatus.NO_CONTENT);
	}

	@Test(priority = 5)
	@CitrusTest
	public void getAuthToken() {
		http().client(localTestClient).send()
				.post("/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
				.contentType("application/x-www-form-urlencoded").payload("client_id=" + initGlobalValues.getClientId()
						+ "&username="+USER_NAME+"&password=password&grant_type=password");
		http().client(localTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
					@Override
					public void validate(Map response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.get("access_token"));
						user_auth_token = (String) response.get("access_token");
						System.out.println("User auth token value ==" + user_auth_token);
					}
				});

	}

	@Test(priority = 6)
	@CitrusTest
	public void getUserTest() {
		http().client(restTestClient).send()
				.get("/v1/user/read/" + userId + "?Fields=completeness,missingFields,topic")
				.accept(Constants.CONTENT_TYPE_APPLICATION_JSON)
				.header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey())
				.contentType(Constants.CONTENT_TYPE_APPLICATION_JSON)
				.header(Constants.X_AUTHENTICATED_USER_TOKEN, user_auth_token);
		http().client(restTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.getId());
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
					}
				});
	}

	private String createUserMap() {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("request", createUserInnerMap());
		try {
			return objectMapper.writeValueAsString(requestMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String createUserWithDuplicateUserName() {
		Map<String, Object> requestMap = new HashMap<>();
		Map<String, Object> innerMap = createUserInnerMap();
		innerMap.put("email", Constants.USER_NAME_PREFIX + UUID.randomUUID().toString() + "@gmail.com");
		requestMap.put("request", innerMap);
		try {
			return objectMapper.writeValueAsString(requestMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String createUserWithDuplicateEmail() {
		Map<String, Object> requestMap = new HashMap<>();
		Map<String, Object> innerMap = createUserInnerMap();
		innerMap.put("userName", Constants.USER_NAME_PREFIX + UUID.randomUUID().toString());
		requestMap.put("request", innerMap);
		try {
			return objectMapper.writeValueAsString(requestMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Map<String, Object> createUserInnerMap() {
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put("firstName", "ft_first_Name_pw12401");
		innerMap.put("lastName", "ft_lastName");
		innerMap.put("password", "password");
		USER_NAME = Constants.USER_NAME_PREFIX + EndpointConfig.val;
		String email = Constants.USER_NAME_PREFIX + EndpointConfig.val + "@gmail.com";
		innerMap.put("userName", USER_NAME);
		innerMap.put("email", email);
		return innerMap;
	}
}
