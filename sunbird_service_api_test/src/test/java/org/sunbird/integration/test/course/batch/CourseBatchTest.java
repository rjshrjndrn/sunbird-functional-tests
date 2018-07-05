package org.sunbird.integration.test.course.batch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseCode;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.user.EndpointConfig;
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

public class CourseBatchTest extends TestNGCitrusTestDesigner {

	@Autowired
	protected HttpClient restTestClient;
	@Autowired
	protected EndpointConfig.TestGlobalProperty initGlobalValues;

	protected ObjectMapper objectMapper = new ObjectMapper();
	protected static String admin_token = null;
	protected static String userId = null;
	protected static String user_auth_token = null;
	protected static volatile String USER_NAME = "userName";

	private static String default_org_id = StringUtils.EMPTY;
	private static List<String> course_ids = Arrays.asList("do_1125131878408273921307", "do_1125131742193909761303");
	private static Map<String, String> batchIdsMap = new ConcurrentHashMap<>();
	private static Map<String, List<String>> batchIDContentIdsConcurrentMap = new ConcurrentHashMap<>();
	private static Map<String, List<String>> batchIDContentIdsActualMap = new HashMap<>();

	private static final String CREATE_USER = "api/user/v1/create";
	private static final String SEARCH_ORG = "/api/org/v1/search";
	private static final String CREATE_COURSE_BATCH = "/api/course/v1/batch/create";
	private static final String ADD_USER_TO_COURSE_BATCH = "/api/course/v1/batch/user/add/";
	private static final String ENROLL_USER_TO_OPEN_BATCH = "/api/course/v1/enrol";
	private static final String UPDATE_CONTENT_STATE = "/api/course/v1/content/state/update";
	private static final String GET_CONTENT_STATE = "/api/course/v1/content/state/read";
	
	@Test()
	@CitrusTest
	public void testCreateUser() {
		String requestJson = createRequestMapFor("user");
		getTestCase().setName("createUser");
		http().client(restTestClient).send().post(CREATE_USER).contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)
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
	/*
	 * Key cloak admin token generation is required for some API to get
	 * authenticated
	 *
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


	@Test(dependsOnMethods = { "testCreateUser", "getAdminAuthToken" })
	@CitrusTest
	/** This method will disable user required action change password under keyCloak.
	 * after disabling that , we can generate newly created user auth token.
	 */
	public void updateUserRequiredLoginActionTest() {
		http()
				.client(restTestClient)
				.send()
				.put("/auth/admin/realms/" + initGlobalValues.getRelam() + "/users/" + userId)
				.header(Constant.AUTHORIZATION, Constant.BEARER + admin_token)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)
				.payload("{\"requiredActions\":[]}");
		http().client(restTestClient).receive().response(HttpStatus.NO_CONTENT);
	}

	@Test(dependsOnMethods = { "updateUserRequiredLoginActionTest" })
	@CitrusTest
	public void getUserAuthToken() {
		http().client(restTestClient).send()
				.post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
				.contentType("application/x-www-form-urlencoded")
				.payload("client_id=" + initGlobalValues.getClientId() + "&username=" + USER_NAME + "@"
						+ initGlobalValues.getSunbirdDefaultChannel() + "&password=password&grant_type=password");
		http().client(restTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
					@Override
					public void validate(Map response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.get("access_token"));
						user_auth_token = (String) response.get("access_token");
					}
				});
	}


	@DataProvider(name = "createBatchDynamicDataProvider")
	public Object[][] createBatchDynamicDataProvider() {
		return new Object[][] {
				new Object[] { createRequestMapFor("batch", "invite-only", course_ids.get(0)),
						Constant.BATCH_TEMPLATE_LOCATION + "course_batch_response.json", "createInviteBasedBatch" },
				new Object[] { createRequestMapFor("batch", "open", course_ids.get(1)),
						Constant.BATCH_TEMPLATE_LOCATION + "course_batch_response.json", "createOpenBatch" } };
	}

	@DataProvider(name = "updateCourseStateDynamicDataProvider")
	public Object[][] updateCourseStateDynamicDataProvider() {
		return new Object[][] {
				new Object[] { createRequestMapFor("updateCourseState", course_ids.get(0), batchIdsMap.get("createInviteBasedBatch")),
						"updateCourseStateForInviteBasedBatch", batchIdsMap.get("createInviteBasedBatch") },
				new Object[] { createRequestMapFor("updateCourseState", course_ids.get(1), batchIdsMap.get("createOpenBatch")),
						"updateCourseStateForOpenBatch", batchIdsMap.get("createOpenBatch") } };
	}

	@DataProvider(name = "getCourseStateDynamicDataProvider")
	public Object[][] getCourseStateDynamicDataProvider() {
		return new Object[][] {
				new Object[] { createRequestMapFor("getCourseStateFailure", course_ids.get(0), batchIdsMap.get("createInviteBasedBatch")),
						"getCourseStateFailure" },
				new Object[] { createRequestMapFor("getCourseState", "getCourseStateForBatchOnly", batchIdsMap.get("createInviteBasedBatch")),
				"getCourseStateForBatchOnly" },
				new Object[] { createRequestMapFor("getCourseState", "getCourseStateForBatchAndCourse", batchIdsMap.get("createOpenBatch"), course_ids.get(1)),
						"getCourseStateForBatchAndCourse" },
				new Object[] { createRequestMapFor("getCourseState", "getCourseStateForContentsOnly"),
				"getCourseStateForContentsOnly" }};
	}

	@Test()
	@CitrusTest
	public void getOrgId() {

		String orgReqPayLoad = createRequestMapFor("org");
		http().client(restTestClient).send().post(SEARCH_ORG)
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
						Assert.assertNotNull(contents);
						Assert.assertEquals(contents.size(), 1);
						default_org_id = (String) contents.get(0).get("id");
						Assert.assertNotNull(default_org_id);

					}
				});
	}

	@Test(dataProvider = "createBatchDynamicDataProvider", dependsOnMethods = { "getUserAuthToken", "getOrgId" })
	@CitrusParameters({ "requestJson", "responseJson", "testName" })
	@CitrusTest
	public void createBatch(String requestJson, String responseJson, String testName) {
	    getTestCase().setName(testName);
		http().client(restTestClient).send().post(CREATE_COURSE_BATCH)
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

	@Test( dependsOnMethods = { "createBatch" })
	@CitrusTest
	public void addUserToInviteOnlyBatch() {
	    getTestCase().setName("addUserToInviteOnlyBatch");
	    String requestJson = createRequestMapFor("AddUser", userId);
	    String batchId = batchIdsMap.get("createInviteBasedBatch");
		http().client(restTestClient).send().post(ADD_USER_TO_COURSE_BATCH+batchId)
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
		        .header(Constant.X_AUTHENTICATED_USER_TOKEN, user_auth_token)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON).payload(requestJson);

		http().client(restTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.getId());
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
		                String userIdVal = (String) response.getResult().get(userId);
		                Assert.assertNotNull(userIdVal);
		                Assert.assertEquals(userIdVal, "SUCCESS");
					}
				});
	}

	@Test( dependsOnMethods = { "createBatch" })
	@CitrusTest
	public void enrollUserToOpenBatch() {
		getTestCase().setName("enrollUserToOpenBatch");
	    String requestJson = createRequestMapFor("EnrolUser", course_ids.get(1), userId, batchIdsMap.get("createOpenBatch"));

		http().client(restTestClient).send().post(ENROLL_USER_TO_OPEN_BATCH)
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
				.header(Constant.X_AUTHENTICATED_USER_TOKEN, user_auth_token)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON).payload(requestJson);

		http().client(restTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.getId());
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
						String responseStr = (String) response.getResult().get("response");
						Assert.assertNotNull(responseStr);
						Assert.assertEquals(responseStr, "SUCCESS");
					}
				});
	}

	@Test(dataProvider = "updateCourseStateDynamicDataProvider", dependsOnMethods = { "createBatch" })
	@CitrusParameters({ "requestJson", "testName" , "batchId"})
	@CitrusTest
	public void updateCourseState(String requestJson, String testName, String batchId) {
		getTestCase().setName(testName);
		batchIDContentIdsActualMap.put(batchId, batchIDContentIdsConcurrentMap.get(batchId));
		batchIDContentIdsConcurrentMap.remove(batchId);
		http().client(restTestClient).send().patch(UPDATE_CONTENT_STATE)
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
				.header(Constant.X_AUTHENTICATED_USER_TOKEN, user_auth_token)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON).payload(requestJson);

		http().client(restTestClient).receive().response(HttpStatus.OK)
				.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.getId());
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
						Map<String, Object> result = response.getResult();
						Assert.assertNotNull(result);
						Set<String> resultValue = result.values().stream()
								.map( Object::toString )
								.collect(Collectors.toSet() );
						Assert.assertEquals(resultValue.size(), 1);
						Assert.assertEquals(resultValue.contains("SUCCESS"), true);
					}
				});

	}

	@Test(dataProvider = "getCourseStateDynamicDataProvider", dependsOnMethods = { "updateCourseState" })
	@CitrusParameters({ "requestJson", "testName" })
	@CitrusTest
	public void getCourseState(String requestJson, String testName) {
		getTestCase().setName(testName);

		http().client(restTestClient).send().post(GET_CONTENT_STATE)
				.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
				.header(Constant.X_AUTHENTICATED_USER_TOKEN, user_auth_token)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON).payload(requestJson);

		HttpClientReceiveActionBuilder httpResponse = http().client(restTestClient).receive();
	    if(testName.equalsIgnoreCase("getCourseStateFailure")) {
	    	httpResponse.response(HttpStatus.BAD_REQUEST)
	          .payload(new ClassPathResource(Constant.BATCH_TEMPLATE_LOCATION+"get_course_state_response_failure.json"));
	    }else {
	    	httpResponse.response(HttpStatus.OK)
			.validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
				@Override
				public void validate(Response response, Map<String, Object> headers, TestContext context) {
					Assert.assertNotNull(response.getId());
					Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
					Map<String, Object> result = response.getResult();
					Assert.assertNotNull(result);
					List<Map<String, Object>> contentList = (List<Map<String, Object>>) result.get("contentList");
					switch(testName) {
						case "getCourseStateForBatchOnly":
							Assert.assertEquals(contentList.size(), 2);
							break;
						case "getCourseStateForBatchAndCourse":
							Assert.assertEquals(contentList.size(), 2);
							break;
						case "getCourseStateForContentsOnly":
							Assert.assertEquals(contentList.size(), 4);
							break;
					}
				}
			});
	    	
	    }
		

	}
	private String createRequestMapFor(String operation, String... arg) {
		Map<String, Object> requestMap = new HashMap<>();
		if (operation.equalsIgnoreCase("org"))
			requestMap.put(Constant.REQUEST, createOrgInnerMap());
		else if (operation.equalsIgnoreCase("batch"))
			requestMap.put(Constant.REQUEST, creatBatchInnerMap(arg));
		else if (operation.equalsIgnoreCase("AddUser"))
			requestMap.put(Constant.REQUEST, createUserInnerMap(arg));
		else if (operation.equalsIgnoreCase("EnrolUser"))
			requestMap.put(Constant.REQUEST, createEnrolUserInnerMap(arg));
		else if (operation.equalsIgnoreCase("updateCourseState"))
			requestMap.put(Constant.REQUEST, updateCourseStateInnerMap(arg));
		else if (operation.equalsIgnoreCase("getCourseState"))
			requestMap.put(Constant.REQUEST, getCourseStateInnerMap(arg));
		else if (operation.equalsIgnoreCase("getCourseStateFailure"))
			requestMap.put(Constant.REQUEST, getCourseStateFailureInnerMap(arg));
		else if(operation.equalsIgnoreCase("user"))
			requestMap.put(Constant.REQUEST,createUserInnerMap());
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
	
	private Map<String, Object> createUserInnerMap(String... arg) {
		Map<String, Object> innerMap = new HashMap<>();
		List<String> userIds = Arrays.asList(arg);
		innerMap.put(Constant.USER_IDS, userIds);
		return innerMap;
	}
	
	private Map<String, Object> createEnrolUserInnerMap(String... arg) {
		Map<String, Object> innerMap = new HashMap<>();
		String batchId = null, userId = null, courseId=null;
		if (arg.length > 0)
			courseId = arg[0];
		if (arg.length > 1)
			userId = arg[1];
		if (arg.length > 2)
			batchId = arg[2];

		innerMap.put(Constant.USER_ID, userId);
		innerMap.put(Constant.BATCH_ID, batchId);
		innerMap.put(Constant.COURSE_ID, courseId);
		return innerMap;
	}
	
	private Map<String, Object> updateCourseStateInnerMap(String... arg) {
		Map<String, Object> innerMap = new HashMap<>();
		List<Map<String, Object>> contents = new ArrayList<>();
		Random random =new Random();
		String batchId = null,  courseId=null;
		if (arg.length > 0)
			courseId = arg[0];
		if (arg.length > 1)
			batchId = arg[1];

		innerMap.put(Constant.USER_ID, userId);
		innerMap.put(Constant.CONTENTS, contents);

		List<String> contentIds = new ArrayList<>();
		batchIDContentIdsConcurrentMap.put(batchId,contentIds);
		int counter=0;
		for(int i=0;i<2;i++) {
			Map<String, Object> content = new HashMap<>();
			String contentId = "test"+(counter++)+random.nextInt(10);
			contentIds.add(contentId);
			content.put("contentId", contentId);
			content.put("status", random.nextInt(3));
			content.put("batchId", batchId);
			content.put("courseId", courseId);
			contents.add(content);
		}

		return innerMap;
	}
	
	private Map<String, Object> getCourseStateFailureInnerMap(String... arg) {
		Map<String, Object> innerMap = new HashMap<>();
		String batchId = null;
		if (arg.length > 0)
			batchId = arg[0];
		innerMap.put(Constant.USER_ID, userId);
		innerMap.put(Constant.BATCH_ID, batchId);
		innerMap.put(Constant.COURSE_IDS, course_ids);
		
		return innerMap;
	}
	
	private Map<String, Object> getCourseStateInnerMap(String... arg) {
		Map<String, Object> innerMap = new HashMap<>();
		String batchId = null,  courseId=null, testName = null;
		if (arg.length > 0)
			testName = arg[0];
		
		switch(testName) {
			case "getCourseStateForBatchOnly":
				if (arg.length > 1)
					batchId = arg[1];
				innerMap.put(Constant.BATCH_ID, batchId);
				 break;
			case "getCourseStateForBatchAndCourse":
				if (arg.length > 1)
					batchId = arg[1];
				if (arg.length > 2)
					courseId = arg[2];
				innerMap.put(Constant.BATCH_ID, batchId);
				innerMap.put(Constant.COURSE_IDS, new ArrayList<>(Arrays.asList(courseId)));
				 break;
			case "getCourseStateForContentsOnly":
				// convert  List<List<String>>(contentIdsMap.values) to List<String>
				Set<String> contentIdList = batchIDContentIdsActualMap.values().stream().flatMap(list -> list.stream()).collect(Collectors.toSet());
				innerMap.put(Constant.CONTENT_IDS, contentIdList);
				 break;
		   default:
			   break;
		}
				
		innerMap.put(Constant.USER_ID, userId);
		
		return innerMap;
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
}
