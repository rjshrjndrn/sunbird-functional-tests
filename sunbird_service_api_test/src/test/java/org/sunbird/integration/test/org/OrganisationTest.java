/**
 * 
 */
package org.sunbird.integration.test.org;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.sunbird.common.annotation.CleanUp;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseCode;
import org.sunbird.common.util.CassandraCleanUp;
import org.sunbird.common.util.Constant;
import org.sunbird.common.util.ElasticSearchCleanUp;
import org.sunbird.common.util.HTTPMethod;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import org.testng.Assert;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder.HttpClientReceiveActionBuilder;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author RKHema
 *
 */
public class OrganisationTest extends BaseCitrusTest {

	private static final String CREATE_ORGANISATION_URI = "/v1/org/create";
	
	private static final String ORGANISATION_TEMPLATE_PATH_CREATE = "templates/organisation/create/";

	public static final String TEST_NAME_CREATE_ROOT_ORG_WITH_EXISTING_CHANNEL = "createRootOrgWithExistingChannel";
	public static final String TEST_NAME_CREATE_ROOT_ORG_WITH_EXISTING_EXTERNAL_ID = "createRootOrgWithExistingExternalId";
	public static final String TEST_NAME_CREATE_ROOT_ORG_WITH_NEW_CHANNEL = "createRootOrgWithNewChannel";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITH_EXISTING_CHANNEL = "createSubOrgWithExistingChannel";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITH_EXTERNAL_ID = "createSubOrgWithExternalId";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITH_INVALIDE_CHANNEL = "createSubOrgWithInvalidChannel";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITH_INVALID_LOCATION_CODE = "createSubOrgWithInvalidLocationCode";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITH_LOCATION_CODE = "createSubOrgWithLocationCode";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITH_NAME = "createSubOrgWithName";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITHOUT_ACCESS_TOKEN = "createSubOrgWithoutAccessToken";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITHOUT_ORG_NAME = "createSubOrgWithoutOrgName";
	public static final String TEST_NAME_CREATE_SUB_ORG_WITH_PROVIDER_WITHOUT_EXTERNAL_ID = "createSubOrgWithProviderWithoutExternalId";

	public static final String REQUEST_JSON = "/request.json";
	public static final String RESPONSE_JSON = "/response.json";

	private ObjectMapper objectMapper = new ObjectMapper();

	List<String> cassandraList = toDeleteCassandraRecordsMap.get("organisation");
	List<String> esList = toDeleteEsRecordsMap.get("organisation");

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
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_NAME +REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_org_success_response.json",
					"TEST_NAME_CREATE_SUB_ORG_WITH_NAME"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_EXISTING_CHANNEL +REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_org_success_response.json",
					"TEST_NAME_CREATE_SUB_ORG_WITH_EXISTING_CHANNEL"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_ROOT_ORG_WITH_NEW_CHANNEL +REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + "create_org_success_response.json",
					"TEST_NAME_CREATE_SUB_ORG_WITH_EXISTING_CHANNEL"
			},

			
		};
	}


	@SuppressWarnings("null")
	@DataProvider(name = "createFailureOrgDataProvider")
	public Object[][] createFailureOrgDataProvider() {

		return new Object[][] {
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITHOUT_ORG_NAME+ REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITHOUT_ORG_NAME+ RESPONSE_JSON,
					"TEST_NAME_CREATE_SUB_ORG_WITHOUT_ORG_NAME"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_PROVIDER_WITHOUT_EXTERNAL_ID + REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_PROVIDER_WITHOUT_EXTERNAL_ID + RESPONSE_JSON,
					"TEST_NAME_CREATE_SUB_ORG_WITH_PROVIDER_WITHOUT_EXTERNAL_ID"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_INVALID_LOCATION_CODE + REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_INVALID_LOCATION_CODE + RESPONSE_JSON,
					"TEST_NAME_CREATE_SUB_ORG_WITH_INVALID_LOCATION_CODE"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_NAME +REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITHOUT_ACCESS_TOKEN + RESPONSE_JSON,
					"TEST_NAME_CREATE_SUB_ORG_WITHOUT_ACCESS_TOKEN"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_ROOT_ORG_WITH_EXISTING_CHANNEL +REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_ROOT_ORG_WITH_EXISTING_CHANNEL + RESPONSE_JSON,
					"TEST_NAME_CREATE_ROOT_ORG_WITH_EXISTING_CHANNEL"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_EXTERNAL_ID +REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_EXTERNAL_ID + RESPONSE_JSON,
					"TEST_NAME_CREATE_SUB_ORG_WITH_EXTERNAL_ID"
			},
			new Object[] {
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_INVALIDE_CHANNEL +REQUEST_JSON,
					ORGANISATION_TEMPLATE_PATH_CREATE + TEST_NAME_CREATE_SUB_ORG_WITH_INVALIDE_CHANNEL + RESPONSE_JSON,
					"TEST_NAME_CREATE_SUB_ORG_WITH_INVALIDE_CHANNEL"
			},
			
			
		};

	}


	@Test(dataProvider = "createSuccessOrgDataProvider", dependsOnMethods = { "getAdminAuthToken" },enabled=true)
	@CitrusParameters({"requestJsonFile","responseJson","testCaseName"})
	@CitrusTest

	public void testCreateOrganisationSuccess(String requestJsonFile,String responseJson,String testCaseName) {

		HttpClientReceiveActionBuilder response = performSuccessTest(testCaseName,HTTPMethod.POST,CREATE_ORGANISATION_URI,requestJsonFile,true,HttpStatus.OK,responseJson);

		handleOrgCreationResponse(response);
	}

	@Test(dataProvider = "createFailureOrgDataProvider", dependsOnMethods = { "getAdminAuthToken" },enabled = true)
	@CitrusTest
	@CitrusParameters({"requestJsonFile","responseJson","testCaseName"})

	public void testCreateOrganisationFailure(String requestJsonFile,String responseJson,String testCaseName) {

		HttpStatus reponseStatus;
		boolean isAccessToken;
		if(!testCaseName.equalsIgnoreCase("TEST_NAME_CREATE_SUB_ORG_WITHOUT_ACCESS_TOKEN")) {
			reponseStatus = HttpStatus.BAD_REQUEST;
			isAccessToken = true;
		}else {
			reponseStatus = HttpStatus.UNAUTHORIZED;
			isAccessToken = false;
			
		}
		performFailureTest(testCaseName,HTTPMethod.POST,CREATE_ORGANISATION_URI,requestJsonFile,isAccessToken,reponseStatus,responseJson);

	}
	@CleanUp
	/** Method to perform the cleanup after test suite completion. */
	public static void cleanUp() {

		ElasticSearchCleanUp elasticSearchCleanUp = ElasticSearchCleanUp.getInstance();
		CassandraCleanUp cassandraCleanUp = CassandraCleanUp.getInstance();

		elasticSearchCleanUp.deleteFromElasticSearch(toDeleteEsRecordsMap);
		cassandraCleanUp.deleteFromCassandra(toDeleteCassandraRecordsMap);


	}
	private void handleOrgCreationResponse(HttpClientReceiveActionBuilder response) {
		response
		.response()
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


}
