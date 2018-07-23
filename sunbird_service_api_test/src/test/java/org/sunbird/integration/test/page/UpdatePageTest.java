package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.PageUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UpdatePageTest extends BaseCitrusTestRunner {

  private static final String PAGE_NAME =
      "FT_Page_Name-" + String.valueOf(System.currentTimeMillis());
  public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME =
      "testCreatePageSuccessWithName";

  public static final String TEST_NAME_UPDATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testUpdatePageFailureWithoutAccessToken";
  public static final String TEST_NAME_UPDATE_PAGE_FAILURE_WITHOUT_PAGE_ID =
      "testUpdatePageFailureWithoutPageId";

  public static final String TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_EXISTING_PAGE_ID =
      "testUpdatePageSuccessWithExistingPageId";
  public static final String TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_PORTAL_MAP =
      "testUpdatePageSuccessWithPortalMap";
  public static final String TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_APP_MAP =
      "testUpdatePageSuccessWithAppMap";

  public static final String TEMPLATE_DIR = "templates/page/update";
  public static final String PAGE_CREATE_TEMPLATE_DIR = "templates/page/create";

  private String getUpdatePageUrl() {
    return getLmsApiUriPath("/api/data/v1/page/update", "/v1/page/update");
  }

  @DataProvider(name = "updatePageFailureDataProvider")
  public Object[][] updatePageFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_UPDATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {TEST_NAME_UPDATE_PAGE_FAILURE_WITHOUT_PAGE_ID, true, HttpStatus.BAD_REQUEST},
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

  @DataProvider(name = "updatePageSuccessDataProvider")
  public Object[][] updatePageSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_EXISTING_PAGE_ID, true, HttpStatus.OK},
      new Object[] {TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_PORTAL_MAP, true, HttpStatus.OK},
      new Object[] {TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_APP_MAP, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "updatePageSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdatePageSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

    getAuthToken(this, isAuthRequired);

    if (testName.equalsIgnoreCase(TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_EXISTING_PAGE_ID)
        || testName.equalsIgnoreCase(TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_PORTAL_MAP)
        || testName.equalsIgnoreCase(TEST_NAME_UPDATE_PAGE_SUCCESS_WITH_APP_MAP)) {
      variable("pageName", PAGE_NAME);
      beforeTestUpdatePage();
    }
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

  private void beforeTestUpdatePage() {
    PageUtil.createPage(
        this,
        testContext,
        PAGE_CREATE_TEMPLATE_DIR,
        TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME,
        HttpStatus.OK);
  }
}
