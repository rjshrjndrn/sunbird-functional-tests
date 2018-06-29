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

import org.springframework.beans.factory.annotation.Autowired;
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
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
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
	
	private static final String SEARCH_ORGANISATION_URI = "/v1/location/delete";
	private static final String CREATE_GEO_LOCATION_URI = "/v1/notification/location/create";


	private static final String ORGANISATION_TEMPLATE_PATH = "templates/organisation/create/";
	private static final String ORGANISATION_TEMPLATE_PATH_UPDATE = "templates/organisation/update/";

	private static String ORG_NAME = "Org-" + String.valueOf(System.currentTimeMillis());
	private static String ORG_CODE = "Org-code-" + String.valueOf(System.currentTimeMillis());
	private static String CHANNEL = "Channel-" + String.valueOf(System.currentTimeMillis());

	private static String PROVIDER = "Provider-" + String.valueOf(System.currentTimeMillis());
	private static String EXTERNAL_ID = "ExtId-" + String.valueOf(System.currentTimeMillis());

	private static String LOCATION_ID = "LocId-" + String.valueOf(System.currentTimeMillis());

	private static String LOCATION_NAME = "LOC-" + String.valueOf(System.currentTimeMillis());
	private static String LOCATION_CODE = "LOC-CODE-" + String.valueOf(System.currentTimeMillis());
	private static String LOCATION = "LOC-" + String.valueOf(System.currentTimeMillis());

	private static String CHANNEL_FOR_EXIST_CHECK;
	private static String EXTERNAL_ID_FOR_EXIST_CHECK;
	private static String PROVIDER_FOR_EXIST_CHECK;

	
	private static Stack<String> stack = new Stack();
	private static String admin_token = null;
	@Autowired private HttpClient restTestClient;
	@Autowired private TestGlobalProperty initGlobalValues;
	private ObjectMapper objectMapper = new ObjectMapper();
	ArrayList<String> orgCreateSuccessTestNames = new ArrayList<String>() {{
		add("Test with only Organisation Name");
		add("Organisation name with Channel");		
		add("Organisation name with Channel, Provider & External Id");
		add("Create an Organisation with existing Location ");
	}};

	ArrayList<String> orgCreateFailureTestNames = new ArrayList<String>() {{
		add("Test without Organisation Name");
		add("Test Organisation Name and code & rootOrg=true");
		add("Test Organisation Name and code , rootOrg=true with provider");
		add("Test Organisation Name and code , rootOrg=true with channel, provider");
		add("Organisation name with Provider & External id");
		add("Organisation name with Location id");
		add("Organisation create without Access Token");
		add("Organisation with the existing Channel");
		add("Organisation with the existing Provider & External id");

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

		http().client(initGlobalValues.getKeycloakUrl()).send()
		.post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
		.contentType("application/x-www-form-urlencoded")
		.payload("client_id=admin-cli&username=" + initGlobalValues.getKeycloakAdminUser() + "&password="
				+ initGlobalValues.getKeycloakAdminPass() + "&grant_type=password");
		http().client(initGlobalValues.getKeycloakUrl()).receive().response(HttpStatus.OK)
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
		
		for (int i = 0; i<=3 ; i++ ) {
		
			String requestJson = createSuccessOrganisationMap(i);
			Object[] testCaseData = { requestJson, i};

			dataProvider[i] = testCaseData;

		}
		
		return dataProvider;
	
	}


	@SuppressWarnings("null")
	@DataProvider(name = "createFailureOrgDataProvider")
	public Object[][] createFailureOrgDataProvider() {


		Object[][] dataProvider = new Object[10][];

		for (int i = 0; i<= 8 ; i++ ) {

			Object[] testCaseData = { createFailureOrganisationMap(i), i};

			dataProvider[i] = testCaseData;

		}

		return dataProvider;
	}

	@Test(dataProvider = "createSuccessOrgDataProvider", dependsOnMethods = { "getAdminAuthToken" })
	@CitrusParameters({"requestJson","count"})
	//@CitrusTest
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

						Assert.assertNotNull(response.getResult().get(Constant.RESPONSE));
						Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);

						String orgId = (String) response.getResult().get(Constant.ORGANISATION_ID);
						Assert.assertNotNull(orgId);


						/* Remove Cassandra & Elastic search data which inserted during Testing*/ 

						if (cassandraList == null) {
							cassandraList = new ArrayList<>();
						}
						cassandraList.add(orgId);
						toDeleteCassandraRecordsMap.put("organisation", cassandraList);

						if (esList == null) {
							esList = new ArrayList<>();
						}
						esList.add(orgId);

						toDeleteEsRecordsMap.put("org", esList);


					}
				});
	}



	@Test(dataProvider = "createFailureOrgDataProvider", dependsOnMethods = { "getAdminAuthToken" })
	@CitrusParameters({"requestJson","count"})
	//@CitrusTest
	/**
	 * Method to test the create functionality of State type (root) location .The scenario are as - 1.
	 * Successful creation of State type location. 2. Try to create state type location with same
	 * location code and expect BAD_REQUEST in response.
	 */
	public void testCreateOrganisationFailure(String requestJson,int count) {


		String testName = orgCreateFailureTestNames.get(count);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		getTestCase().setName(testName);
		HttpClientRequestActionBuilder httpRequest = http()
				.client(restTestClient)
				.send()
				.post(CREATE_ORGANISATION_URI)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)		
				//.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token)
				.payload(requestJson);

		if (count != 6 ) {
			httpRequest.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token);
		}


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
		LOCATION_ID = "LocId-" + String.valueOf(System.currentTimeMillis());

		Map<String, Object> requestMap = new HashMap<>();
		Map<String, Object> innerMap = new HashMap<>();

		switch (count) {
		case 0:
			/* without organisation Name */

			break;
		case 1:
			/* Organisation name, Org code, and Rootorg as true without (channel && (Provider & External Id)*/
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
			/* orgname , location id which is not existing */
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.LOCATION_ID, LOCATION_ID);	
			break;
		case 6:
			/* With out authentication -  */
			innerMap.put(Constant.ORG_NAME, ORG_NAME);

			break;
		case 7:
			/* Test case if the channel is already exists */

			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);	
			innerMap.put(Constant.IS_ROOT_ORG, true);
			innerMap.put(Constant.CHANNEL, CHANNEL_FOR_EXIST_CHECK);
			break;
		case 8:

			/* Test case if the ( Provider & External ID ) is already exists */			
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);	
			innerMap.put(Constant.IS_ROOT_ORG, true);
			innerMap.put(Constant.CHANNEL, CHANNEL);
			innerMap.put(Constant.PROVIDER, PROVIDER_FOR_EXIST_CHECK);
			innerMap.put(Constant.EXTERNAL_ID, EXTERNAL_ID_FOR_EXIST_CHECK);

			break;
		case 9:

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
			/* Organisation name, Org code, and channel which not is exists */
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

			/* To use for the 7th Failure Use case */			
			CHANNEL_FOR_EXIST_CHECK = ORG_NAME;
			EXTERNAL_ID_FOR_EXIST_CHECK = EXTERNAL_ID;
			PROVIDER_FOR_EXIST_CHECK = PROVIDER;
			/* To use for the 7th Failure Use case */

			break;
		case 3:
			/* Create a Existing Location and pass to Org creation */
			
			LOCATION_ID = createLocation();
			innerMap.put(Constant.ORG_NAME, ORG_NAME);
			innerMap.put(Constant.ORG_CODE, ORG_CODE);
			innerMap.put(Constant.LOCATION_ID, LOCATION_ID);
			
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

		elasticSearchCleanUp.deleteFromElasticSearch(toDeleteEsRecordsMap);
		cassandraCleanUp.deleteFromCassandra(toDeleteCassandraRecordsMap);


	}

	private String createLocation() {
		
		
		Map<String, Object> requestMap = new HashMap<>();
		Map<String, Object> innerMap = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();
		List data = new ArrayList();
		
		innerMap.put(Constant.ROOT_ORG_ID, "ORG_001");
		
		dataMap.put(Constant.LOCATION, LOCATION);
		dataMap.put(Constant.LOCATION_TYPE, "state");
		
		data.add(dataMap);
		innerMap.put(Constant.DATA, data);
		requestMap.put(Constant.REQUEST, innerMap);
		
		
		String locationJson = null;

		try {
			locationJson = objectMapper.writeValueAsString(requestMap);
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		http()
		.client(restTestClient)
		.send()
		.post(CREATE_GEO_LOCATION_URI)
		.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)		
		.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token)
		.payload(locationJson);


		http()
		.client(restTestClient)
		.receive()
		.response(HttpStatus.OK)
		.validationCallback(
				new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
					@Override
					public void validate(
							Response response, Map<String, Object> headers, TestContext context) {
						Assert.assertNotNull(response.getResult().get(Constant.RESPONSE));
						LOCATION_ID = (String) response.getResult().get(Constant.ID);
						Assert.assertNotNull(LOCATION_ID);
					}
				});

		
		return LOCATION_ID;

	}

}
