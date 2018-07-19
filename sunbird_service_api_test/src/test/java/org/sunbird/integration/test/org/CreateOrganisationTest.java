package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
  public static final String TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_PROVIDER_AND_EXTERNAL_ID =
      "testCreateSubOrgSuccessWithProviderAndExternalId";
  public static final String TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_EXISTING_CHANNEL =
      "testCreateSubOrgSuccessWithExistingChannel";
  public static final String TEST_NAME_CREATE_ORG_SUCCESS_WITH_ORG_NAME_AND_NEW_CHANNEL =
      "testCreateOrgSuccessWithOrgNameAndNewChannel";
  private static final Object TEST_NAME_CREATE_ORG_SUCCESS_WITH_PROVIDER_AND_EXTERNALID =
      "testCreateOrgSuccessWithProviderAndExternalId";
  public static final String TEST_NAME_CREATE_ORG_FAILURE_WITH_ORG_NAME_AND_EXISTING_CHANNEL =
      "testCreateOrgFailureWithOrgNameAndExistingChannel";
  private static final Object TEST_NAME_CREATE_ORG_FAILURE_WITH_ORG_NAME_AND_EXISTING_EXTERNAL_ID =
      "testCreateOrgFailureWithOrgNameAndExistingExternalId";

  public static final String TEMPLATE_DIR = "templates/organisation/create";

  private String getCreateOrgUrl() {
    return getLmsApiUriPath("/api/org/v1/create", "/v1/org/create");
  }

  @DataProvider(name = "createSubOrgFailureDataProvider")
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

  @Test(dataProvider = "createSubOrgFailureDataProvider")
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

  @DataProvider(name = "createSubOrgSuccessDataProvider")
  public Object[][] createOrgSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_ORG_NAME, true, HttpStatus.OK},
      new Object[] {
        TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_PROVIDER_AND_EXTERNAL_ID, true, HttpStatus.OK
      },
      new Object[] {TEST_NAME_CREATE_SUB_ORG_SUCCESS_WITH_EXISTING_CHANNEL, true, HttpStatus.OK},
    };
  }

  @Test(
    dataProvider = "createSubOrgSuccessDataProvider",
    dependsOnMethods = {"testCreateRootOrganisation"}
  )
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateOrganisationSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    variable("rootChannel", OrgUtil.getRootChannel());
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

  @DataProvider(name = "createRootOrgDataProvider")
  public Object[][] createRootOrgDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_CREATE_ORG_SUCCESS_WITH_ORG_NAME_AND_NEW_CHANNEL, true, HttpStatus.OK
      },
      new Object[] {TEST_NAME_CREATE_ORG_SUCCESS_WITH_PROVIDER_AND_EXTERNALID, true, HttpStatus.OK},
      new Object[] {
        TEST_NAME_CREATE_ORG_FAILURE_WITH_ORG_NAME_AND_EXISTING_CHANNEL,
        true,
        HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_ORG_FAILURE_WITH_ORG_NAME_AND_EXISTING_EXTERNAL_ID,
        true,
        HttpStatus.BAD_REQUEST
      }
    };
  }

  @Test(dataProvider = "createRootOrgDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateRootOrganisation(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);

    variable("rootChannel", OrgUtil.getRootChannel());
    variable("rootExternalId", OrgUtil.getRootExternalId());
    beforeTest();

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

  private void beforeTest() {
    OrgUtil.getRootOrgId(this, testContext);
  }
}
