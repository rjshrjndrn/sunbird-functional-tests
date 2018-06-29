package org.sunbird.integration.test.course.batch;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseCode;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseUserAuthTest;
import org.sunbird.integration.test.user.EndpointConfig;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.core.JsonProcessingException;

public class CourseBatchTest extends TestNGCitrusTestDesigner {

	private static String default_org_id = StringUtils.EMPTY;
	private static List<String> course_ids = Arrays.asList("do_1125131878408273921307", "do_1125131742193909761303");
	private static Map<String, String> batchIdsMap = new HashMap<>();

	//
	@Autowired
	protected HttpClient restTestClient;
	//@Autowired
	protected EndpointConfig.TestGlobalProperty initGlobalValues = new EndpointConfig().initGlobalValues();

	protected ObjectMapper objectMapper = new ObjectMapper();
	protected static String admin_token = null;
	protected static String userId = null;
	protected static String user_auth_token = null;
	protected static volatile String USER_NAME = "userName";
	String local_auth = "http://localhost:8080";

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

	@Test(dependsOnMethods = {"testCreateUser", "getAdminAuthToken"})
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

	@Test(dependsOnMethods = {"updateUserRequiredLoginActionTest"})
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
		USER_NAME = Constant.USER_NAME_PREFIX + EndpointConfig.val;
		String email = Constant.USER_NAME_PREFIX + EndpointConfig.val + "@gmail.com";
		innerMap.put(Constant.USER_NAME, USER_NAME);
		innerMap.put(Constant.EMAIL, email);
		return innerMap;
	}
	//

	@DataProvider(name = "createBatchDynamicDataProvider")
	public Object[][] createUserDynamicJsonData() {
		return new Object[][] {
				new Object[] { createRequestMapFor("batch", "invite-only", course_ids.get(0)),
						Constant.BATCH_TEMPLATE_LOCATION + "course_batch_response.json", "createInviteBasedBatch" },
				new Object[] { createRequestMapFor("batch", "open", course_ids.get(1)),
						Constant.BATCH_TEMPLATE_LOCATION + "course_batch_response.json", "createOpenBatch" } };
	}

	@Test(dependsOnMethods = { "getUserAuthToken" })
	@CitrusTest
	public void test() {
		
	}
	
	@Test()
	@CitrusTest
	public void getOrgId() {

		String orgReqPayLoad = createRequestMapFor("org");
		http().client(restTestClient).send().put("/v1/org/search")
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON).payload(orgReqPayLoad);

		http().client(restTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.getId());
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
						Assert.assertNotNull(response.getResult().get("response"));
						Map<String, Object> responseMap = (Map<String, Object>) response.getResult().get("response");
						List<Map<String, Object>> contents = (List<Map<String, Object>>) responseMap.get("content");
						Assert.assertEquals(contents.size(), 1);
						default_org_id = (String) contents.get(0).get("id");
						Assert.assertNotNull(default_org_id);
					}
				});
		System.out.println(batchIdsMap);
	}

	@Test(dataProvider = "createBatchDynamicDataProvider", dependsOnMethods = { "getOrgId","getUserAuthToken" })
	@CitrusParameters({ "requestJson", "responseJson", "testName" })
	@CitrusTest
	public void createBatch(String requestJson, String responseJson, String testName) {
	    getTestCase().setName(testName);
		http().client(restTestClient).send().put("/v1/course/batch/create")
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
		        .header(Constant.X_AUTHENTICATED_USER_TOKEN, user_auth_token)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON).payload(requestJson);

		http().client(restTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.getId());
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
						Assert.assertNotNull(response.getResult().get("response"));
		                String batchId = (String) response.getResult().get("batchId");
		                Assert.assertNotNull(batchId);
		                batchIdsMap.put(testName, batchId);
					}
				});
	}

	private String createRequestMapFor(String operation, String... arg) {
		Map<String, Object> requestMap = new HashMap<>();
		if (operation.equalsIgnoreCase("org"))
			requestMap.put(Constant.REQUEST, createOrgInnerMap());
		else if (operation.equalsIgnoreCase("batch"))
			requestMap.put(Constant.REQUEST, creatBatchInnerMap(arg));
		else
			requestMap.put(Constant.REQUEST, new HashMap<>());
		try {
			return objectMapper.writeValueAsString(requestMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Map<String, Object> creatBatchInnerMap(String... arg) {

		String enrollment = null, courseId = null;
		if (arg.length > 0)
			enrollment = arg[0];
		if (arg.length > 1)
			courseId = arg[1];
		Map<String, Object> innerMap = new HashMap<>();

		/*
		 * "enrollmentType": "invite-only",
		 */
		innerMap.put(Constant.NAME, "ft_Batch");
		innerMap.put(Constant.DESCRIPTION, "FT batch description");
		innerMap.put(Constant.CREATEDFOR, Arrays.asList(default_org_id));
		innerMap.put(Constant.ENROLLMENT_TYPE, enrollment);
		innerMap.put(Constant.COURSE_ID, courseId);
		innerMap.put("startDate", LocalDate.now().toString());
		innerMap.put("endDate", LocalDate.now().plusDays(60L).toString());
		return innerMap;
	}

	private Map<String, Object> createOrgInnerMap() {
		Map<String, Object> innerMap = new HashMap<>();
		Map<String, Object> filter = new HashMap<>();
		filter.put(Constant.CHANNEL, initGlobalValues.getSunbirdDefaultChannel());
		filter.put(Constant.IS_ROOT_ORG, true);
		innerMap.put(Constant.FILTERS, filter);
		innerMap.put(Constant.LIMIT, 1);
		return innerMap;
	}
}
