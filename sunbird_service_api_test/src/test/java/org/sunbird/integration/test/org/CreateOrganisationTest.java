package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateOrganisationTest extends BaseCitrusTest {

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

  @Test(dataProvider = "createOrgFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateOrganisationFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getCreateOrgUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
