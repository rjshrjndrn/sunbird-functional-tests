package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UpdatePageTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_UPDATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testUpdatePageFailureWithoutAccessToken";
  public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_PAGE_ID =
      "testUpdatePageFailureWithoutPageId";

  public static final String TEMPLATE_DIR = "templates/page/update";

  private String getUpdatePageUrl() {

    return getLmsApiUriPath("/api/data/v1/page/update", "/v1/page/update");
  }

  @DataProvider(name = "updatePageFailureDataProvider")
  public Object[][] updatePageFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_UPDATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_PAGE_ID, true, HttpStatus.BAD_REQUEST},
    };
  }

  @Test(dataProvider = "updatePageFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdatePageFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performPatchTest(
        this,
        TEMPLATE_DIR,
        testName,
        getUpdatePageUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
