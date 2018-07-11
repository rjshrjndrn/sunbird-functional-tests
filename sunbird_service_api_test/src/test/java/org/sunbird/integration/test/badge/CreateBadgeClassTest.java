package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateBadgeClassTest extends BaseCitrusTest {
  public static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITH_INVALID_ROOT_ORG_ID =
      "testCreateBadgeClassFailureWithInvalidRootOrgId";

  public static final String TEMPLATE_DIR = "templates/badge/class/create";

  private String getCreateBadgeClassUrl() {
    return getLmsApiUriPath("/api/badging/v1/issuer/badge/create", "/v1/issuer/badge/create");
  }

  @DataProvider(name = "createBadgeClassDataProviderFailure")
  public Object[][] createBadgeClassDataProviderFailure() {
    return new Object[][] {
      new Object[] {
        TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITH_INVALID_ROOT_ORG_ID, HttpStatus.BAD_REQUEST
      }
    };
  }

  @Test(dataProvider = "createBadgeClassDataProviderFailure")
  @CitrusParameters({"testName", "responseCode"})
  @CitrusTest
  public void testCreateBadgeClassFailure(String testName, HttpStatus responseCode) {
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getCreateBadgeClassUrl(),
        REQUEST_FORM_DATA,
        responseCode,
        RESPONSE_JSON,
        false);
  }
}
