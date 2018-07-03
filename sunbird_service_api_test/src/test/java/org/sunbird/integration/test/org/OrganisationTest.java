/**
 * 
 */
package org.sunbird.integration.test.org;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author RKHema
 *
 */
public class OrganisationTest extends BaseCitrusTest {


	private static final String CREATE_ORGANISATION_URI = "/v1/org/create";
	private static String admin_token = null;
	@Autowired private HttpClient restTestClient;
	@Autowired private TestGlobalProperty initGlobalValues;

	private static final String ORGANISATION_TEMPLATE_PATH_CREATE = "templates/organisation/create/";

	private ObjectMapper objectMapper = new ObjectMapper();

	List<String> cassandraList = toDeleteCassandraRecordsMap.get("organisation");
	List<String> esList = toDeleteEsRecordsMap.get("org");

	/**
	 * 
	 * Key cloak admin token generation is required for some API to get authenticated
	 * 
	 */
	@Test()
	@CitrusTest
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
	/*
	 * Data provider for Success Test 
	 * 
	 * 
	 */

	@SuppressWarnings("null")
	@DataProvider(name = "createSuccessOrgDataProvider")
	public Object[][] createSuccessOrgDataProvider() {

		return new Object[][] {
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_with_name_request.json",
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_org_success_response.json",
					"CreateSubOrgWithName"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_with_existing_channel_request.json",
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_org_success_response.json",
					"CreateSubOrgWithExistingChannel"
			},
		};
	}
	

	@SuppressWarnings("null")
	@DataProvider(name = "createFailureOrgDataProvider")
	public Object[][] createFailureOrgDataProvider() {
		
		return new Object[][] {
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_without_orgname_request.json",
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_without_orgname_response.json",
					"CreateSubOrgWithName"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_with_provider_without_externalid_request.json",
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_with_provider_without_externalid_response.json",
					"CreateSubOrgWithProviderWithoutExternalId"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_with_invalid_locationCode_request.json",
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_with_invalid_locationCode_response.json",
					"CreateSubOrgWithInvalidLocationCode"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_with_name_request.json",
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_suborg_without_access_token_response.json",
					"CreateSubOrgWithoutAccessToken"
			},

		};
		
	}


	@Test(dataProvider = "createSuccessOrgDataProvider", dependsOnMethods = { "getAdminAuthToken" },enabled=true)
	@CitrusParameters({"requestJsonFile","responseJson","testCaseName"})
	@CitrusTest
	/**
	 * Method to test the create functionality of State type (root) location .The scenario are as - 1.
	 * Successful creation of State type location. 2. Try to create state type location with same
	 * location code and expect BAD_REQUEST in response.
	 */
	public void testCreateOrganisationSuccess(String requestJsonFile,String responseJson,String testCaseName) {

		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			// TODO: handle exception
		}

		http()
		.client(restTestClient)
		.send()
		.post(CREATE_ORGANISATION_URI)
		.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)
		.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
		.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token)
		.payload(new ClassPathResource(requestJsonFile));

		http()
		.client(restTestClient)
		.receive()
		.response(HttpStatus.OK)
		.payload(new ClassPathResource(responseJson))
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
						System.out.println(" Cassandra List " + cassandraList);
						toDeleteCassandraRecordsMap.put("organisation", cassandraList);

						if (esList == null) {
							esList = new ArrayList<>();
						}
						esList.add(orgId);

						toDeleteEsRecordsMap.put("org", esList);


					}
				});
	}

	@Test(dataProvider = "createFailureOrgDataProvider", dependsOnMethods = { "getAdminAuthToken" },enabled = true)
	@CitrusTest
	@CitrusParameters({"requestJsonFile","responseJson","testCaseName"})

	/**
	 * Method to test the create functionality of State type (root) location .The scenario are as - 1.
	 * Successful creation of State type location. 2. Try to create state type location with same
	 * location code and expect BAD_REQUEST in response.
	 */
	public void testCreateOrganisationFailure(String requestJsonFile,String responseJson,String testCaseName) {

		
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		getTestCase().setName(testCaseName);
		HttpClientRequestActionBuilder httpRequest = http()
				.client(restTestClient)
				.send()
				.post(CREATE_ORGANISATION_URI)
				.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)	
				.payload(new ClassPathResource(requestJsonFile));
		
				if(!testCaseName.equalsIgnoreCase("CreateSubOrgWithoutAccessToken")) {
					httpRequest.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token);
				}
				
				
		
		HttpClientReceiveActionBuilder httpResponse = http()
         .client(restTestClient)
         .receive();
		if(!testCaseName.equalsIgnoreCase("CreateSubOrgWithoutAccessToken")) {
			httpResponse.response(HttpStatus.BAD_REQUEST)
			.payload(new ClassPathResource(responseJson));
		}else {
			httpResponse.response(HttpStatus.UNAUTHORIZED)
			.payload(new ClassPathResource(responseJson));
		}
			
	}
	@CleanUp
	/** Method to perform the cleanup after test suite completion. */
	public static void cleanUp() {

		ElasticSearchCleanUp elasticSearchCleanUp = ElasticSearchCleanUp.getInstance();
		CassandraCleanUp cassandraCleanUp = CassandraCleanUp.getInstance();

		elasticSearchCleanUp.deleteFromElasticSearch(toDeleteEsRecordsMap);
		cassandraCleanUp.deleteFromCassandra(toDeleteCassandraRecordsMap);


	}

}
