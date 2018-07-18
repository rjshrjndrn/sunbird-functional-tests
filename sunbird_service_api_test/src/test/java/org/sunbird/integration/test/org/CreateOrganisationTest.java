package org.sunbird.integration.test.org;

import javax.ws.rs.core.MediaType;

import org.springframework.http.HttpStatus;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;

public class CreateOrganisationTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITHOUT_NAME =
      "testCreateSubOrgFailureWithoutName";
  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_PROVIDER_WITHOUT_EXTERNAL_ID =
      "testCreateSubOrgFailureWithProviderWithoutExternalId";
  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_LOCATION_CODE =
      "testCreateSubOrgFailureWithInvalidLocationCode";
  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_CHANNEL =
      "testCreateSubOrgFailureWithInvalidChannel";
  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_EXTERNAL_ID_WITHOUT_PROVIDER =
      "testCreateSubOrgFailureWithExternalIdWithoutProvider";
  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testCreateSubOrgFailureWithoutAccessToken";
  public static final String TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_ORG_NAME =
	      "testCreateSubOrgSuccessWithOrgName";
  public static final String TEST_NAME_CREATE_ORG_SUCCESS_WITH_ORG_NAME_AND_CHANNEL =
	      "testCreateOrgSuccessWithOrgNameAndChannel";
  public static final String TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_PROVIDER_AND_EXTERNAL_ID =
	      "testCreateSubOrgSuccessWithProviderAndExternalId";
  public static final String TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_ESISTING_PROVIDER_AND_EXTERNAL_ID =
	      "testCreateSubOrgSuccessWithExistingProviderAndExternalId";
  

  public static final String TEMPLATE_DIR = "templates/organisation/create";

  private String getCreateOrgUrl() {

    return getLmsApiUriPath("/api/org/v1/create", "/v1/org/create");
  }

  @DataProvider(name = "createOrgFailureDataProvider")
  public Object[][] createOrgFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_CREATE_SUB_ORG_FAILURE_WITHOUT_NAME, true, HttpStatus.BAD_REQUEST},
      new Object[] {
        TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_PROVIDER_WITHOUT_EXTERNAL_ID,
        true,
        HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_LOCATION_CODE, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_CHANNEL, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_EXTERNAL_ID_WITHOUT_PROVIDER,
        true,
        HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_SUB_ORG_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      }
    };
  }

  @Test(dataProvider = "createOrgFailureDataProvider",enabled=false)
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateOrganisationFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateOrgUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
  
  @DataProvider(name = "createOrgSuccessDataProvider")
  public Object[][] createOrgSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_ORG_NAME, true, HttpStatus.OK},
      new Object[] {TEST_NAME_CREATE_ORG_SUCCESS_WITH_ORG_NAME_AND_CHANNEL, true, HttpStatus.OK},
      new Object[] {TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_PROVIDER_AND_EXTERNAL_ID, true, HttpStatus.OK},
      new Object[] {TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_ESISTING_PROVIDER_AND_EXTERNAL_ID, true, HttpStatus.OK},
      
    };
  }
  
  @Test(dataProvider = "createOrgSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateOrganisationSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    if(testName.equalsIgnoreCase(TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_ESISTING_PROVIDER_AND_EXTERNAL_ID)) {
    	OrgUtil.createOrg(this, testContext, TEMPLATE_DIR, "testCreateOrgSuccessWithOrgNameAndChannel", HttpStatus.OK);
    	
    }
    
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateOrgUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
