package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReadAllPagesSettingsTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_READ_ALL_PAGE_SETTINGS_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testReadAllPageSettingsFailureWithoutAccessToken";

  public static final String TEMPLATE_DIR = "templates/page/read";

  private String getReadAllPageSettingsUrl() {
    return getLmsApiUriPath("/api/data/v1/", "/v1/page/all/settings");
  }

  @DataProvider(name = "readAllPageSettingsFailureDataProvider")
  public Object[][] readAllPageSettingsFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_READ_ALL_PAGE_SETTINGS_FAILURE_WITHOUT_ACCESS_TOKEN,
        false,
        HttpStatus.UNAUTHORIZED,
      },
    };
  }

  @Test(dataProvider = "readAllPageSettingsFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testAllReadPageSettingsFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);

    performGetTest(
        this,
        TEMPLATE_DIR,
        testName,
        getReadAllPageSettingsUrl(),
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
