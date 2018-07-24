package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.UserUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UpdateUserLoginTimeTest extends BaseCitrusTestRunner {

  public static final String TEST_UPDATE_USER_LOGIN_TIME_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testUpdateUserLoginTimeFailureWithoutAccessToken";

  public static final String TEST_UPDATE_USER_LOGIN_TIME_SUCCESS_WITH_INVALID_USER_ID =
      "testUpdateUserLoginTimeSuccessWithInvalidUserId";
  public static final String TEST_UPDATE_USER_LOGIN_TIME_SUCCESS_WITH_VALID_USER_ID =
      "testUpdateUserLoginTimeSuccessWithvalidUserId";
  public static final String TEMPLATE_DIR = "templates/user/loginTime";

  private String getUpdaterUserLoginTimeUrl() {
    return getLmsApiUriPath("/api/user/v1/update/logintime", "/v1/user/update/logintime");
  }

  @DataProvider(name = "updateUserLoginTimeFailureDataProvider")
  public Object[][] updateUserLoginTimeFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_UPDATE_USER_LOGIN_TIME_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      }
    };
  }

  @DataProvider(name = "updateUserLoginTimeSuccessDataProvider")
  public Object[][] updateUserLoginTimeSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_UPDATE_USER_LOGIN_TIME_SUCCESS_WITH_VALID_USER_ID, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "updateUserLoginTimeFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUserUpdateLoginTimeFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performPatchTest(
        this,
        TEMPLATE_DIR,
        testName,
        getUpdaterUserLoginTimeUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test(dataProvider = "updateUserLoginTimeSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdateUserLoginTimeSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    beforeTest();
    performPatchTest(
        this,
        TEMPLATE_DIR,
        testName,
        getUpdaterUserLoginTimeUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testUpdateUserLoginTimeSuccess() {
    getAuthToken(this, true);
    performPatchTest(
        this,
        TEMPLATE_DIR,
        TEST_UPDATE_USER_LOGIN_TIME_SUCCESS_WITH_INVALID_USER_ID,
        getUpdaterUserLoginTimeUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.OK,
        RESPONSE_JSON);
  }

  private void beforeTest() {
    UserUtil.getUserId(this, testContext);
  }
}
