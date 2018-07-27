package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.TestActionUtil;
import org.sunbird.common.action.UserUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.Test;

public class ReadUserProfileVisibiltyTest extends BaseCitrusTestRunner {
  public static final String TEMPLATE_DIR = "templates/user/getbyuserid";
  private static final String GET_USER_BY_ID_SERVER_URI = "/api/user/v1/read/";
  private static final String GET_USER_BY_ID_LOCAL_URI = "/v1/user/read/";
  public static final String TEMPLATE_DIR_PROFILE_VISIBILITY =
      "templates/user/profilevisibility/update";
  public static final String TEST_BA_USER_PROFILE_VISIBILITY_SUCCESS_WITH_VALID_USERID =
      "testUpdateUserProfileVisibilitySuccessWithValidUserId";

  @Test()
  @CitrusTest
  public void testGetUserProfileVisibilitySuccess() {
    getTestCase().setName("testGetUserProfileVisibilitySuccessWithValidUserId");
    beforeTest();
    setProfileVisibility();
    performGetTest(
        this,
        TEMPLATE_DIR,
        "testGetUserProfileVisibilitySuccessWithValidUserId",
        getLmsApiUriPath(
            GET_USER_BY_ID_SERVER_URI,
            GET_USER_BY_ID_LOCAL_URI,
            TestActionUtil.getVariable(testContext, "userId")),
        true,
        HttpStatus.OK,
        RESPONSE_JSON);
  }

  private void setProfileVisibility() {
    UserUtil.setProfileVisibilityPrivate(
        this,
        TEMPLATE_DIR_PROFILE_VISIBILITY,
        TEST_BA_USER_PROFILE_VISIBILITY_SUCCESS_WITH_VALID_USERID);
  }

  private void beforeTest() {
    getAuthToken(this, true);
    UserUtil.getUserId(this, testContext);
    variable("userId", testContext.getVariable("userId"));
  }
}
