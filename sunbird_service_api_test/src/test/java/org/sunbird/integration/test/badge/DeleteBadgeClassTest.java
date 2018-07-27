package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.BadgeClassUtil;
import org.sunbird.common.action.IssuerUtil;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.common.action.TestActionUtil;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DeleteBadgeClassTest extends BaseCitrusTestRunner {
  private static final String BT_TEST_NAME_CREATE_ISSUER_SUCCESS = "testCreateIssuerSuccess";
  private static final String BT_CREATE_ISSUER_TEMPLATE_DIR = "templates/badge/issuer/create";

  private static final String BT_TEST_NAME_CREATE_BADGE_CLASS_SUCCESS_WITH_TYPE_USER =
      "testCreateBadgeClassSuccessWithTypeUser";
  private static final String BT_CREATE_BADGE_CLASS_TEMPLATE_DIR = "templates/badge/class/create";

  private static final String TEST_NAME_DELTE_BADGE_CLASS_SUCCESS = "testDeleteBadgeClassSuccess";

  private static final String TEST_NAME_DELETE_BADGE_CLASS_FAILURE_WITH_INVALID_ID =
      "testDeleteBadgeClassFailureWithInvalidId";
  private static final String TEST_NAME_DELETE_BADGE_CLASS_FAILURE_WITH_BLANK_ID =
      "testDeleteBadgeClassFailureWithBlankId";

  private static final String TEMPLATE_DIR = "templates/badge/class/delete";

  private String getReadBadgeClassUrl(String pathParam) {
    return getLmsApiUriPath(
        "/api/badging/v1/issuer/badge/delete", "/v1/issuer/badge/delete", pathParam);
  }

  @DataProvider(name = "deleteBadgeClassDataProviderSuccess")
  public Object[][] deleteBadgeClassDataProviderSuccess() {
    return new Object[][] {new Object[] {TEST_NAME_DELTE_BADGE_CLASS_SUCCESS}};
  }

  @DataProvider(name = "deleteBadgeClassDataProviderFailure")
  public Object[][] deleteBadgeClassDataProviderFailure() {
    return new Object[][] {
      new Object[] {
        TEST_NAME_DELETE_BADGE_CLASS_FAILURE_WITH_INVALID_ID, "invalid", Constant.RESPONSE_JSON
      },
      new Object[] {TEST_NAME_DELETE_BADGE_CLASS_FAILURE_WITH_BLANK_ID, "", null}
    };
  }

  @Test(dataProvider = "deleteBadgeClassDataProviderSuccess")
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testDeleteBadgeClassSuccess(String testName) {
    beforeTest();
    performDeleteTest(
        this,
        TEMPLATE_DIR,
        testName,
        getReadBadgeClassUrl(
            TestActionUtil.getVariable(testContext, Constant.EXTRACT_VAR_BADGE_ID)),
        null,
        null,
        false,
        HttpStatus.OK,
        RESPONSE_JSON);
    afterTest();
  }

  @Test(dataProvider = "deleteBadgeClassDataProviderFailure")
  @CitrusParameters({"testName", "badgeId", "responseJson"})
  @CitrusTest
  public void testDeleteBadgeClassFailure(String testName, String badgeId, String responseJson) {
    performDeleteTest(
        this,
        TEMPLATE_DIR,
        testName,
        getReadBadgeClassUrl(badgeId),
        null,
        null,
        false,
        HttpStatus.NOT_FOUND,
        responseJson);
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
    BadgeClassUtil.createBadgeClass(
        this,
        testContext,
        config,
        BT_CREATE_BADGE_CLASS_TEMPLATE_DIR,
        BT_TEST_NAME_CREATE_BADGE_CLASS_SUCCESS_WITH_TYPE_USER,
        HttpStatus.OK);
  }

  private void afterTest() {}
}
