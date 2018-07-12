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
  public static final String TEST_NAME_CREATE_SUB_ORG_WITHOUT_ACCESS_TOKEN =
      "testCreateSubOrgWithoutAccessToken";


  public static final String TEMPLATE_DIR = "templates/organisation/create";

  private String getCreateOrgUrl() {

    return getLmsApiUriPath("/org/v1/create", "/v1/org/create");
  }

  @DataProvider(name = "createFailureOrgDataProvider")
  public Object[][] createFailureOrgDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_CREATE_SUB_ORG_FAILURE_WITHOUT_NAME},
      new Object[] {TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_PROVIDER_WITHOUT_EXTERNAL_ID},
      new Object[] {TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_LOCATION_CODE},
      new Object[] {TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_CHANNEL},
      new Object[] {TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_EXTERNAL_ID_WITHOUT_PROVIDER},
      new Object[] {TEST_NAME_CREATE_SUB_ORG_WITHOUT_ACCESS_TOKEN}
    };
  }

  @Test(dataProvider = "createFailureOrgDataProvider")
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testCreateOrganisationFailure(String testName) {

    boolean isAuthRequired = true;
    HttpStatus httpStatusCode;

    if (testName.equalsIgnoreCase(TEST_NAME_CREATE_SUB_ORG_WITHOUT_ACCESS_TOKEN)) {
      isAuthRequired = false;
      httpStatusCode = HttpStatus.UNAUTHORIZED;
    } else {
      httpStatusCode = HttpStatus.BAD_REQUEST;
    }

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
