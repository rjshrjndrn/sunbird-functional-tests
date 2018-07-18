package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
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

  public static final String TEMPLATE_DIR = "templates/organisation/read";

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
}
