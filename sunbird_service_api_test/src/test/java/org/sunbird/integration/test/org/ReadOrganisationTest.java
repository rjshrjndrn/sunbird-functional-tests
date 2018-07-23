package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import java.util.Random;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReadOrganisationTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_READ_ORG_FAILURE_WITHOUT_ORG_ID =
      "testReadOrgFailureWithoutOrgId";
  public static final String TEST_NAME_READ_ORG_FAILURE_WITH_INVALID_ORG_ID =
      "testReadOrgFailureWithInvalidOrgId";
  public static final String TEST_NAME_READ_ORG_FAILURE_WITH_PROVIDER_WITHOUT_EXTERNAL_ID =
      "testReadOrgFailureWithProviderWithoutExternalId";
  public static final String TEST_NAME_READ_ORG_FAILURE_WITH_EXTERNAL_ID_WITHOUT_PROVIDER =
      "testReadOrgFailureWithExternalIdWithoutProvider";
  public static final String TEST_NAME_READ_ORG_FAILURE_WITH_INVALID_PROVIDER_AND_EXTERNAL_ID =
      "testReadOrgFailureWithInvalidProviderAndExternalId";
  public static final String TEST_NAME_READ_ORG_SUCCESS_WITH_VALID_ORG_ID =
      "testReadOrgSuccessWithValidOrgId";
  public static final String TEST_NAME_READ_ORG_SUCCESS_WITH_VALID_PROVIDER_AND_EXTERNAL_ID =
      "testReadOrgSuccessWithValidProviderAndExternalId";

  public static final String TEMPLATE_DIR = "templates/organisation/read";
  private static String orgId = null;
  private static final String externalId = "FT_Org_External_" + (new Random()).nextInt(100);

  private String getSearchOrgUrl() {

    return getLmsApiUriPath("/api/org/v1/read", "/v1/org/read");
  }

  @DataProvider(name = "readOrgFailureDataProvider")
  public Object[][] readOrgFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_READ_ORG_FAILURE_WITHOUT_ORG_ID, HttpStatus.BAD_REQUEST},
      new Object[] {TEST_NAME_READ_ORG_FAILURE_WITH_INVALID_ORG_ID, HttpStatus.NOT_FOUND},
      new Object[] {
        TEST_NAME_READ_ORG_FAILURE_WITH_INVALID_PROVIDER_AND_EXTERNAL_ID, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_READ_ORG_FAILURE_WITH_PROVIDER_WITHOUT_EXTERNAL_ID, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_READ_ORG_FAILURE_WITH_EXTERNAL_ID_WITHOUT_PROVIDER, HttpStatus.BAD_REQUEST
      },
    };
  }

  @Test(dataProvider = "readOrgFailureDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testReadOrganisationFailure(String testName, HttpStatus httpStatusCode) {

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchOrgUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @DataProvider(name = "readOrgSuccessDataProvider")
  public Object[][] readOrgSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_READ_ORG_SUCCESS_WITH_VALID_ORG_ID, HttpStatus.OK},
      new Object[] {TEST_NAME_READ_ORG_SUCCESS_WITH_VALID_PROVIDER_AND_EXTERNAL_ID, HttpStatus.OK}
    };
  }

  @Test(dataProvider = "readOrgSuccessDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testReadOrganisationSuccess(String testName, HttpStatus httpStatusCode) {
    variable("rootOrgExternalId", externalId);
    beforeTest();
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchOrgUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        httpStatusCode,
        RESPONSE_JSON);
  }

  private void beforeTest() {
    if (StringUtils.isBlank(orgId)) {
      getAuthToken(this, true);
      OrgUtil.createOrg(
          this,
          testContext,
          "templates/organisation/create",
          "testCreateRootOrgSuccessWithProviderAndExternalId",
          HttpStatus.OK);
      orgId = testContext.getVariable("organisationId");
    } else {
      variable("organisationId", orgId);
    }
  }
}
