/**
 * 
 */
package org.sunbird.integration.test.org;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.bcel.classfile.ConstantDouble;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.annotation.CleanUp;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseCode;
import org.sunbird.common.util.CassandraCleanUp;
import org.sunbird.common.util.Constant;
import org.sunbird.common.util.ElasticSearchCleanUp;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder.HttpClientReceiveActionBuilder;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author RKHema
 *
 */
public class OrganisationTest extends BaseCitrusTest {

	/**
	 * @param args
	 */
	private static final String CREATE_ORGANISATION_URI = "/v1/org/create";
	private static final String UPDATE_ORGANISATION_URI = "/api/data/v1/location/update";
	private static final String SEARCH_ORGANISATION_URI = "/api/data/v1/location/delete";

	private static final String ORGANISATION_TEMPLATE_PATH = "templates/organisation/create/";
	private static final String ORGANISATION_TEMPLATE_PATH_UPDATE = "templates/organisation/update/";

	private static String ORG_NAME = "Org-" + String.valueOf(System.currentTimeMillis());
	private static String ORG_CODE = "Org-code-" + String.valueOf(System.currentTimeMillis());
	private static String CHANNEL = "Channel-" + String.valueOf(System.currentTimeMillis());

	private static String PROVIDER = "Provider-" + String.valueOf(System.currentTimeMillis());
	private static String EXTERNAL_ID = "ExtId-" + String.valueOf(System.currentTimeMillis());

	private static Stack<String> stack = new Stack();
	private static String admin_token = null;
	@Autowired private HttpClient restTestClient;
	@Autowired private TestGlobalProperty initGlobalValues;
	private ObjectMapper objectMapper = new ObjectMapper();
	ArrayList<String> orgCreateSuccessTestNames = new ArrayList<String>() {{
		add("Test with only Organisation Name");
		add("Organisation name with Channel");		
		add("Organisation name with Channel, Provider & External Id");

	}};

	ArrayList<String> orgCreateFailureTestNames = new ArrayList<String>() {{
		add("Test without Organisation Name");
		add("Test Organisation Name and code & rootOrg=true");
		add("Test Organisation Name and code , rootOrg=true with provider");
		add("Test Organisation Name and code , rootOrg=true with channel, provider");
		add("Organisation name with Provider & External id");
	}};

	List<String> cassandraList = toDeleteCassandraRecordsMap.get("organisation");
	List<String> esList = toDeleteEsRecordsMap.get("organisation");

	@Test()
	@CitrusTest
	/**
	 * Key cloak admin token generation is required for some API to get authenticated
	 * 
	 */
	public void getAdminAuthToken() {
		http().client("http://localhost:8080").send()
		.post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
		.contentType("application/x-www-form-urlencoded")
		.payload("client_id=admin-cli&username=" + initGlobalValues.getKeycloakAdminUser() + "&password="
				+ initGlobalValues.getKeycloakAdminPass() + "&grant_type=password");
		http().client("http://localhost:8080").receive().response(HttpStatus.OK)
		.validationCallback(new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
			@Override
			public void validate(Map response, Map<String, Object> headers, TestContext context) {
				Assert.assertNotNull(response.get("access_token"));
				admin_token = (String) response.get("access_token");
			}
		});
	}

	@SuppressWarnings("null")
	@DataProvider(name = "createSuccessOrgDataProvider")
	public Object[][] createSuccessOrgDataProvider() {

	
		Object[][] dataProvider = new Object[10][];

		for (int i = 0; i<=2 ; i++ ) {

			Object[] testCaseData = { createSuccessOrganisationMap(i), i};

			dataProvider[i] = testCaseData;

		}

		return dataProvider;
	}


	@SuppressWarnings("null")
	@DataProvider(name = "createFailureOrgDataProvider")
	public Object[][] createFailureOrgDataProvider() {

	
		Object[][] dataProvider = new Object[10][];

		for (int i = 0; i<= 4 ; i++ ) {

			Object[] testCaseData = { createFailureOrganisationMap(i), i};

			dataProvider[i] = testCaseData;

		}
		System.out.println(" Print the full array :::::::" + Arrays.deepToString(dataProvider));
		return dataProvider;
	}

	@Test(dataProvider = "createSuccessOrgDataProvider", dependsOnMethods = { "getAdminAuthToken" })
	@CitrusParameters({"requestJson","count"})
	@CitrusTest
	/**
	 * Method to test the create functionality of State type (root) location .The scenario are as - 1.
	 * Successful creation of State type location. 2. Try to create state type location with same
	 * location code and expect BAD_REQUEST in response.
	 */
	public void testCreateOrganisationSuccess(String requestJson,int count) {

		String testName = orgCreateSuccessTestNames.get(count);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		getTestCase().setName(testName);
		http()
		.client(restTestClient)
		.send()
		.post(CREATE_ORGANISATION_URI)
		.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)
		.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
		.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token)
		.payload(requestJson);

		http()
		.client(restTestClient)
		.receive()
		.response(HttpStatus.OK)
		.validationCallback(
				new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(
							Response response, Map<String, Object> headers, TestContext context) {
						//After success if need to validate anything
						Assert.assertNotNull(response.getResult().get(Constant.RESPONSE));
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);

						String orgId = (String) response.getResult().get(Constant.ORGANISATION_ID);
						Assert.assertNotNull(orgId);


						if (cassandraList == null) {
							cassandraList = new ArrayList<>();
						}
						cassandraList.add(orgId);
						toDeleteCassandraRecordsMap.put("organisation", cassandraList);

						if (esList == null) {
							esList = new ArrayList<>();
						}
						esList.add(orgId);
						esList.forEach(System.out::println);
						toDeleteEsRecordsMap.put("organisation", esList);


					}
				});
	}



	@Test(dataProvider = "createFailureOrgDataProvider", dependsOnMethods = { "getAdminAuthToken" })
	@CitrusParameters({"requestJson","count"})
	@CitrusTest
	/**
	 * Method to test the create functionality of State type (root) location .The scenario are as - 1.
	 * Successful creation of State type location. 2. Try to create state type location with same
	 * location code and expect BAD_REQUEST in response.
	 */
	public void testCreateOrganisationFailure(String requestJson,int count) {

		System.out.println(" Sunbird Rest Clinet ::::::::: " + restTestClient.toString());
		String testName = orgCreateFailureTestNames.get(count);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		getTestCase().setName(testName);
		http()
		.client(restTestClient)
		.send()
		.post(CREATE_ORGANISATION_URI)
		.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)
		.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
		.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token)
		.payload(requestJson);

		http()
		.client(restTestClient)
		.receive()
		.response(HttpStatus.BAD_REQUEST)

		.validationCallback(
				new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(
							Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNull(response.getResult().get(Constant.RESPONSE));

					}
				});
	}

	private String createFailureOrganisationMap(int count) {
		ORG_NAME = "Org-" + String.valueOf(System.currentTimeMillis()+count);
		ORG_CODE = "Org-code-" + String.valueOf(System.currentTimeMillis()+count);
		CHANNEL = "Channel-" + String.valueOf(System.currentTimeMillis()+count);
		PROVIDER = "Provider-" + String.valueOf(System.currentTimeMillis()+count);
		EXTERNAL_ID = "ExtId-" + String.valueOf(System.currentTimeMillis()+count);

		Map<String, Object> requestMap = new HashMap<>();
		Map<String, Object> innerMap = new HashMap<>();

		switch (count) {
		case 0:
			/* without organisation Name */

			break;
		case 1:
			/* Organisation name, Org code, and Rootorg as true without (channel || (Provider & External Id)*/
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);	
			innerMap.put(Constant.IS_ROOT_ORG, true);

			break;
		case 2:
			/* Organisation name, Org code, and Rootorg as true without only Provider not External Id*/
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);	
			innerMap.put(Constant.IS_ROOT_ORG, true);
			innerMap.put(Constant.PROVIDER, PROVIDER);

			break;
		case 3:
			/* Organisation name, Org code, and rootOrg with ( Channel, (Provider & not External ID)  ) */
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);	
			innerMap.put(Constant.IS_ROOT_ORG, true);
			innerMap.put(Constant.CHANNEL, CHANNEL);
			innerMap.put(Constant.PROVIDER, PROVIDER);

			break;
		case 4:
			/* Organisation name, Org code, and rootOrg with (Provider & External ID) as new */
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);	
			innerMap.put(Constant.IS_ROOT_ORG, true);
			innerMap.put(Constant.PROVIDER, PROVIDER);
			innerMap.put(Constant.EXTERNAL_ID, EXTERNAL_ID);
		case 5:
			/* With out authentication */

			break;

		}



		requestMap.put(Constant.REQUEST, innerMap);
		try {
			return objectMapper.writeValueAsString(requestMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/* Success Case */
	private String createSuccessOrganisationMap(int count) {
		ORG_NAME = "Org-" + String.valueOf(System.currentTimeMillis()+count);
		ORG_CODE = "Org-code-" + String.valueOf(System.currentTimeMillis()+count);
		CHANNEL = "Channel-" + String.valueOf(System.currentTimeMillis()+count);
		PROVIDER = "Provider-" + String.valueOf(System.currentTimeMillis()+count);
		EXTERNAL_ID = "ExtId-" + String.valueOf(System.currentTimeMillis()+count);

		Map<String, Object> requestMap = new HashMap<>();
		Map<String, Object> innerMap = new HashMap<>();

		switch (count) {
		case 0:
			/* Only with Organization Name */
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			break;
		case 1:
			/* Organisation name, Org code, and chennel which is exists */
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);	
			innerMap.put(Constant.IS_ROOT_ORG, true);
			innerMap.put(Constant.CHANNEL, CHANNEL);
			break;
		case 2:
			/* Organisation name, Org code, and rootOrg with ( Channel, (Provider & External ID) as new ) */
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);	
			innerMap.put(Constant.IS_ROOT_ORG, true);
			innerMap.put(Constant.CHANNEL, CHANNEL);
			innerMap.put(Constant.PROVIDER, PROVIDER);
			innerMap.put(Constant.EXTERNAL_ID, EXTERNAL_ID);
			break;
		case 3:

			break;
		case 4:

			break;

		}



		requestMap.put(Constant.REQUEST, innerMap);
		try {
			return objectMapper.writeValueAsString(requestMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	@CleanUp
	/** Method to perform the cleanup after test suite completion. */
	public static void cleanUp() {
		
		ElasticSearchCleanUp elasticSearchCleanUp = ElasticSearchCleanUp.getInstance();
		CassandraCleanUp cassandraCleanUp = CassandraCleanUp.getInstance();
		System.out.println("Cassandra Map :::::::: " + toDeleteCassandraRecordsMap);
		elasticSearchCleanUp.deleteFromElasticSearch(toDeleteEsRecordsMap);
		cassandraCleanUp.deleteFromCassandra(toDeleteCassandraRecordsMap);


	}

}
