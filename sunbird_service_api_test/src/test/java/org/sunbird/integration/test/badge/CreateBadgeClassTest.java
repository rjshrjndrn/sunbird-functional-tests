package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.IssuerUtil;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateBadgeClassTest extends BaseCitrusTestRunner {
  public static final String BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS = "testCreateRootOrgSuccess";
  public static final String BT_TEST_NAME_CREATE_ISSUER_SUCCESS = "testCreateIssuerSuccess";

  public static final String BT_CREATE_ISSUER_TEMPLATE_DIR = "templates/badge/issuer/create";
  public static final String BT_CREATE_ORG_TEMPLATE_DIR = "templates/organisation/create";

  public static final String TEST_NAME_CREATE_BADGE_CLASS_SUCCESS_WITH_TYPE_USER =
      "testCreateBadgeClassSuccessWithTypeUser";

  public static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITH_INVALID_ROOT_ORG_ID =
      "testCreateBadgeClassFailureWithInvalidRootOrgId";

  public static final String TEMPLATE_DIR = "templates/badge/class/create";

  private String getCreateBadgeClassUrl() {
    return getLmsApiUriPath("/api/badging/v1/issuer/badge/create", "/v1/issuer/badge/create");
  }

  @DataProvider(name = "createBadgeClassDataProviderSuccess")
  public Object[][] createBadgeClassDataProviderSuccess() {
    return new Object[][] {new Object[] {TEST_NAME_CREATE_BADGE_CLASS_SUCCESS_WITH_TYPE_USER}};
  }

  @DataProvider(name = "createBadgeClassDataProviderFailure")
  public Object[][] createBadgeClassDataProviderFailure() {
    return new Object[][] {
      new Object[] {
        TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITH_INVALID_ROOT_ORG_ID, HttpStatus.BAD_REQUEST
      }
    };
  }

  @Test(dataProvider = "createBadgeClassDataProviderSuccess")
  @CitrusParameters({"testName"})
  @CitrusTest(name = "testName")
  public void testCreateBadgeClassSuccess(String testName) {
    beforeTest();
    performMultipartTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateBadgeClassUrl(),
        REQUEST_FORM_DATA,
        null,
        false,
        HttpStatus.OK,
        RESPONSE_JSON);
    afterTest();
  }

  @Test(dataProvider = "createBadgeClassDataProviderFailure")
  @CitrusParameters({"testName", "responseCode"})
  @CitrusTest(name = "testName")
  public void testCreateBadgeClassFailure(String testName, HttpStatus responseCode) {
    performMultipartTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateBadgeClassUrl(),
        REQUEST_FORM_DATA,
        null,
        false,
        responseCode,
        RESPONSE_JSON);
  }

  private void beforeTest() {
    getAuthToken(this, true);
    IssuerUtil.createIssuer(
        this,
        testContext,
        config,
        BT_CREATE_ISSUER_TEMPLATE_DIR,
        BT_TEST_NAME_CREATE_ISSUER_SUCCESS,
        HttpStatus.OK);
    variable("rootOrgChannel", OrgUtil.getRootOrgChannel());
    OrgUtil.getRootOrgId(this, testContext);
  }

  private void afterTest() {}
}
