package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.TestActionUtil;
import org.sunbird.common.action.UserUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UserProfileVisibilityTest extends BaseCitrusTestRunner {
  public static final String TEST_USER_PROFILE_VISIBILITY_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testUserProfileVisibilityFailureWithoutAccessToken";

  public static final String TEST_USER_PROFILE_VISIBILITY_FAILURE_WITH_INVALID_USERID =
      "testUserProfileVisibilityFailureWithInvalidUserId";
  public static final String TEST_USER_PROFILE_VISIBILITY_SUCCESS_WITH_VALID_USERID =
      "testUserProfileVisibilitySuccessWithValidUserId";
  public static final String TEST_USER_PROFILE_VISIBILITY_FAILURE_WITH_INVALID_COLUMN =
      "testUserProfileVisibilityFailureWithInvalidColumn";

  public static final String TEMPLATE_DIR = "templates/user/profileVisibility";

  private String getUserProfileVisibilityUrl() {
    return getLmsApiUriPath("/api/user/v1/profile/visibility", "/v1/user/profile/visibility");
  }

  @DataProvider(name = "profileVisibilityUserFailureDataProvider")
  public Object[][] profileVisibilityUserailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_USER_PROFILE_VISIBILITY_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {
        TEST_USER_PROFILE_VISIBILITY_FAILURE_WITH_INVALID_USERID, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_USER_PROFILE_VISIBILITY_FAILURE_WITH_INVALID_COLUMN, true, HttpStatus.BAD_REQUEST
      },
    };
  }

  @DataProvider(name = "profileVisibilityUserSuccessDataProvider")
  public Object[][] profileVisibilityUserSuccessDataProvider() {
    return new Object[][] {
      new Object[] {TEST_USER_PROFILE_VISIBILITY_SUCCESS_WITH_VALID_USERID, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "profileVisibilityUserFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testProfileVisibilityUserFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    beforeTest();
    variable("userId", TestActionUtil.getVariable(testContext, "userId"));
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getUserProfileVisibilityUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test(dataProvider = "profileVisibilityUserSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testProfileVisibilityUserLoginTimeSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    beforeTest();
    variable("userId", TestActionUtil.getVariable(testContext, "userId"));
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getUserProfileVisibilityUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  private void beforeTest() {
    UserUtil.getUserId(this, testContext);
  }
}
