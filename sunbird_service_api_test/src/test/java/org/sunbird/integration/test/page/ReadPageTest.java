package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReadPageTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_READ_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testReadPageFailureWithoutAccessToken";

  public static final String TEMPLATE_DIR = "templates/page/read";

  private String getReadPageUrl() {
    return getLmsApiUriPath("/api/data/v1/page/read", "/v1/page/read");
  }

  private String getAllReadPageUrl() {
    return getLmsApiUriPath("/api/data/v1/page/read", "/v1/page/all/settings");
  }

  @DataProvider(name = "readPageFailureDataProvider")
  public Object[][] readPageFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_READ_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
    };
  }

  @Test(dataProvider = "readPageFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testReadPageFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performGetTest(
        this,
        TEMPLATE_DIR,
        testName,
        getAllReadPageUrl(),
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
