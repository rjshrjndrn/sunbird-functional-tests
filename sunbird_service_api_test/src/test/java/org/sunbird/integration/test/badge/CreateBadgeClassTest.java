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
  private static final String BT_TEST_NAME_CREATE_ISSUER_SUCCESS = "testCreateIssuerSuccess";
  private static final String BT_CREATE_ISSUER_TEMPLATE_DIR = "templates/badge/issuer/create";

  private static final String TEST_NAME_CREATE_BADGE_CLASS_SUCCESS_WITH_TYPE_USER =
      "testCreateBadgeClassSuccessWithTypeUser";

  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_ISSUER_ID =
      "testCreateBadgeClassFailureWithoutIssuerId";
  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_NAME =
      "testCreateBadgeClassFailureWithoutName";
  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_CRITERIA =
      "testCreateBadgeClassFailureWithoutCriteria";
  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_DESCRIPTION =
      "testCreateBadgeClassFailureWithoutDescription";
  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_ROOT_ORG_ID =
      "testCreateBadgeClassFailureWithoutRootOrgId";
  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_ROLES =
      "testCreateBadgeClassFailureWithoutRoles";
  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_TYPE =
      "testCreateBadgeClassFailureWithoutType";
  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_SUBTYPE =
      "testCreateBadgeClassFailureWithoutSubtype";

  private static final String TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITH_INVALID_ROOT_ORG_ID =
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
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_ISSUER_ID},
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_NAME},
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_CRITERIA},
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_DESCRIPTION},
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_ROOT_ORG_ID},
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_ROLES},
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_TYPE},
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITHOUT_SUBTYPE},
      new Object[] {TEST_NAME_CREATE_BADGE_CLASS_FAILURE_WITH_INVALID_ROOT_ORG_ID}
    };
  }

  @Test(dataProvider = "createBadgeClassDataProviderSuccess")
  @CitrusParameters({"testName"})
  @CitrusTest
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
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testCreateBadgeClassFailure(String testName) {
    performMultipartTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateBadgeClassUrl(),
        REQUEST_FORM_DATA,
        null,
        false,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  private void beforeTest() {
    getAuthToken(this, true);
    variable("rootOrgChannel", OrgUtil.getRootOrgChannel());
    OrgUtil.getRootOrgId(this, testContext);
    IssuerUtil.createIssuer(
        this,
        testContext,
        config,
        BT_CREATE_ISSUER_TEMPLATE_DIR,
        BT_TEST_NAME_CREATE_ISSUER_SUCCESS,
        HttpStatus.OK);
  }

  private void afterTest() {}
}
