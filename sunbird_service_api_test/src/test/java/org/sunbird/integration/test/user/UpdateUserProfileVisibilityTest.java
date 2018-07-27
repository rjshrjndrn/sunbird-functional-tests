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

public class UpdateUserProfileVisibilityTest extends BaseCitrusTestRunner {
  public static final String TEST_UPDATE_USER_PROFILE_VISIBILITY_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testUpdateUserProfileVisibilityFailureWithoutAccessToken";

  public static final String TEST_UPDATE_USER_PROFILE_VISIBILITY_FAILURE_WITH_INVALID_USERID =
      "testUpdateUserProfileVisibilityFailureWithInvalidUserId";
  public static final String TEST_UPDATE_USER_PROFILE_VISIBILITY_SUCCESS_WITH_VALID_USERID =
      "testUpdateUserProfileVisibilitySuccessWithValidUserId";
  public static final String TEST_UPDATE_USER_PROFILE_VISIBILITY_FAILURE_WITH_INVALID_COLUMN =
      "testUpdateUserProfileVisibilityFailureWithInvalidColumn";
  public static final String
      TEST_UPDATE_USER_PROFILE_VISIBILITY_FAILURE_WITH_SAME_COLUMN_IN_PRIVATE_AND_PUBLIC_ARRAY =
          "testUpdateUserProfileVisibilityFailureWithSameColumnInPrivateAndPublic";
  public static final String
      TEST_UPDATE_USER_PROFILE_VISIBILITY_SUCCESS_WITH_ALREADY_PUBLIC_COLUMN =
          "testUpdateUserProfileVisibilitySuccessWithAlreadyPublicColumn";
  public static final String
      TEST_UPDATE_USER_PROFILE_VISIBILITY_SUCCESS_WITH_ALREADY_PRIVATE_COLUMN =
          "testUpdateUserProfileVisibilitySuccessWithAlreadyPrivateColumn";

  public static final String TEMPLATE_DIR = "templates/user/profilevisibility/update";

  private String getUserProfileVisibilityUrl() {
    return getLmsApiUriPath("/api/user/v1/profile/visibility", "/v1/user/profile/visibility");
  }

  @DataProvider(name = "updateUserProfileVisibilityFailureDataProvider")
  public Object[][] updateUserProfileVisibilityFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_UPDATE_USER_PROFILE_VISIBILITY_FAILURE_WITHOUT_ACCESS_TOKEN,
        false,
        HttpStatus.UNAUTHORIZED
      },
      new Object[] {
        TEST_UPDATE_USER_PROFILE_VISIBILITY_FAILURE_WITH_INVALID_USERID,
        true,
        HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_UPDATE_USER_PROFILE_VISIBILITY_FAILURE_WITH_INVALID_COLUMN,
        true,
        HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_UPDATE_USER_PROFILE_VISIBILITY_FAILURE_WITH_SAME_COLUMN_IN_PRIVATE_AND_PUBLIC_ARRAY,
        true,
        HttpStatus.BAD_REQUEST
      },
    };
  }

  @DataProvider(name = "updateUserProfileVisibilitySuccessDataProvider")
  public Object[][] updateUserProfileVisibilitySuccessDataProvider() {
    return new Object[][] {
      new Object[] {
        TEST_UPDATE_USER_PROFILE_VISIBILITY_SUCCESS_WITH_VALID_USERID, true, HttpStatus.OK
      },
      new Object[] {
        TEST_UPDATE_USER_PROFILE_VISIBILITY_SUCCESS_WITH_ALREADY_PUBLIC_COLUMN, true, HttpStatus.OK
      },
      new Object[] {
        TEST_UPDATE_USER_PROFILE_VISIBILITY_SUCCESS_WITH_ALREADY_PRIVATE_COLUMN, true, HttpStatus.OK
      },
    };
  }

  @Test(dataProvider = "updateUserProfileVisibilityFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdateUserProfileVisibilityFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getTestCase().setName(testName);
    beforeTest(isAuthRequired);
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

  @Test(dataProvider = "updateUserProfileVisibilitySuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdateUserProfileVisibilitySuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getTestCase().setName(testName);
    beforeTest(isAuthRequired);
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

  private void beforeTest(boolean isAuthRequired) {
    getAuthToken(this, isAuthRequired);
    UserUtil.getUserId(this, testContext);
    variable("userId", TestActionUtil.getVariable(testContext, "userId"));
  }
}
