package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.PageUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReadPageSettingsTest extends BaseCitrusTestRunner {

  private static final String PAGE_NAME =
      "FT_Page_Name-" + String.valueOf(System.currentTimeMillis());
  public static final String BT_TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME =
      "testCreatePageSuccessWithName";

  public static final String TEST_NAME_READ_PAGE_SETTINGS_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testReadPageSettingsFailureWithoutAccessToken";
  public static final String TEST_NAME_READ_PAGE_SETTINGS_FAILURE_WITH_INVALID_PAGE_ID =
      "testReadPageSettingsFailureWithInvalidPageId";

  public static final String TEST_NAME_READ_PAGE_SETTINGS_SUCCESS_WITH_VALID_PAGE_ID =
      "testReadPageSettingsSuccessWithValidPageId";

  public static final String TEMPLATE_DIR = "templates/page/read";
  public static final String PAGE_CREATE_TEMPLATE_DIR = "templates/page/create";

  private String getReadPageUrl(String pathParam) {
    return getLmsApiUriPath("/api/data/v1/page/read", "/v1/page/read", pathParam);
  }

  @DataProvider(name = "readPageSettingsFailureDataProvider")
  public Object[][] readPageSettingsFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_READ_PAGE_SETTINGS_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED,
      },
      new Object[] {
        TEST_NAME_READ_PAGE_SETTINGS_FAILURE_WITH_INVALID_PAGE_ID, false, HttpStatus.NOT_FOUND,
      },
    };
  }

  @Test(dataProvider = "readPageSettingsFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testReadPageSettingsFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);

    String url = getReadPageUrl("/invalid");

    performGetTest(
        this, TEMPLATE_DIR, testName, url, isAuthRequired, httpStatusCode, RESPONSE_JSON);
  }

  @DataProvider(name = "readPageSettingsSuccessDataProvider")
  public Object[][] readPageSettingsSuccessDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_READ_PAGE_SETTINGS_SUCCESS_WITH_VALID_PAGE_ID, true, HttpStatus.OK, true
      }
    };
  }

  @Test(dataProvider = "readPageSettingsSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode", "canCreatePage"})
  @CitrusTest
  public void testReadPageSettingsSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode, boolean canCreatePage) {

    getAuthToken(this, isAuthRequired);
    beforeTestPageSettings(canCreatePage);
    String url = getReadPageUrl("/" + PAGE_NAME);

    performGetTest(
        this, TEMPLATE_DIR, testName, url, isAuthRequired, httpStatusCode, RESPONSE_JSON);
  }

  private void beforeTestPageSettings(boolean canCreatePage) {
    if (canCreatePage) {
      variable("pageName", PAGE_NAME);
      PageUtil.createPage(
          this,
          testContext,
          PAGE_CREATE_TEMPLATE_DIR,
          BT_TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME,
          HttpStatus.OK);
    }
  }
}
