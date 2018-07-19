package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreatePageTest extends BaseCitrusTestRunner {

  public static final String PAGE_TEST_NAME_CREATE_ROOT_ORG_SUCCESS = "testCreateRootOrgSuccess";

  public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testCreatePageFailureWithoutAccessToken";
  public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_NAME =
      "testCreatePageFailureWithoutName";

  public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME =
      "testCreatePageSuccessWithName";
  public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME_AND_ORG_ID =
      "testCreatePageSuccessWithNameAndOrgId";

  public static final String TEMPLATE_DIR = "templates/page/create";
  public static final String ORG_CREATE_ORG_TEMPLATE_DIR = "templates/organisation/create";

  private String getCreatePageUrl() {

    return getLmsApiUriPath("/api/data/v1/page/create", "/v1/page/create");
  }

  @DataProvider(name = "createPageFailureDataProvider")
  public Object[][] createPageFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_NAME, true, HttpStatus.BAD_REQUEST},
    };
  }

  @Test(dataProvider = "createPageFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreatePageFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
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
      new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME, true, HttpStatus.OK},
      new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME_AND_ORG_ID, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "createPageSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreatePageSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, true);
    beforeTest();
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

  private void beforeTest() {
    getAuthToken(this, true);
    OrgUtil.createOrg(
        this,
        testContext,
        ORG_CREATE_ORG_TEMPLATE_DIR,
        PAGE_TEST_NAME_CREATE_ROOT_ORG_SUCCESS,
        HttpStatus.OK);
  }
}
