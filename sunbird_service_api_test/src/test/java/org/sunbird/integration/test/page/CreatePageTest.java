package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.common.action.PageUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreatePageTest extends BaseCitrusTestRunner {

  public static final String BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS = "testCreateRootOrgSuccess";

  private static final String PAGE_NAME =
      "FT_Page_Name-" + String.valueOf(System.currentTimeMillis());

  public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testCreatePageFailureWithoutAccessToken";
  public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_NAME =
      "testCreatePageFailureWithoutName";
  public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITH_EXISTING_NAME =
      "testCreatePageFailureWithExistingName";

  public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME =
      "testCreatePageSuccessWithName";
  public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME_AND_ORG_ID =
      "testCreatePageSuccessWithNameAndOrgId";
  public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_PORTAL_MAP =
      "testCreatePageSuccessWithPortalMap";
  public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_APP_MAP =
      "testCreatePageSuccessWithAppMap";

  public static final String TEMPLATE_DIR = "templates/page/create";
  public static final String ORG_CREATE_ORG_TEMPLATE_DIR = "templates/organisation/create";

  private String getCreatePageUrl() {
    return getLmsApiUriPath("/api/data/v1/page/create", "/v1/page/create");
  }

  @DataProvider(name = "createPageFailureDataProvider")
  public Object[][] createPageFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED, false
      },
      new Object[] {
        TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_NAME, true, HttpStatus.BAD_REQUEST, false
      },
      new Object[] {
        TEST_NAME_CREATE_PAGE_FAILURE_WITH_EXISTING_NAME, true, HttpStatus.BAD_REQUEST, true
      },
    };
  }

  @Test(dataProvider = "createPageFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode", "canCreatePage"})
  @CitrusTest
  public void testCreatePageFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode, boolean canCreatePage) {
    getAuthToken(this, isAuthRequired);
    beforeTestCreatePage(testName, false, canCreatePage);

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreatePageUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @DataProvider(name = "createPageSuccessDataProvider")
  public Object[][] createPageSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME, true, HttpStatus.OK, false},
      new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME_AND_ORG_ID, true, HttpStatus.OK, true},
      new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_PORTAL_MAP, true, HttpStatus.OK, false},
      new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_APP_MAP, true, HttpStatus.OK, false},
    };
  }

  @Test(dataProvider = "createPageSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode", "canCreateOrg"})
  @CitrusTest
  public void testCreatePageSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode, boolean canCreateOrg) {

    getAuthToken(this, true);

    beforeTestCreatePage(testName, canCreateOrg, false);

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreatePageUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  private void beforeTestCreatePage(String testName, boolean canCreateOrg, boolean canCreatePage) {

    if (canCreateOrg) {
      variable("rootChannel", OrgUtil.getRootChannel());
      variable("rootExternalId", OrgUtil.getRootExternalId());
      OrgUtil.createOrg(
          this,
          testContext,
          ORG_CREATE_ORG_TEMPLATE_DIR,
          BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS,
          HttpStatus.OK);
    }

    if (canCreatePage) {
      variable("pageName", PAGE_NAME);
      PageUtil.createPage(
          this,
          testContext,
          TEMPLATE_DIR,
          TEST_NAME_CREATE_PAGE_FAILURE_WITH_EXISTING_NAME,
          HttpStatus.OK);
    }
  }
}
