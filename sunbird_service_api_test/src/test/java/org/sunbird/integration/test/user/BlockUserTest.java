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

public class BlockUserTest extends BaseCitrusTestRunner {

  public static final String TEST_BLOCK_USER_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testBlockUserFailureWithoutAccessToken";
  public static final String TEST_BLOCK_USER_FAILURE_WITH_INVALID_USERID =
      "testBlockUserFailureWithInvalidUserId";
  public static final String TEST_BLOCK_USER_SUCCESS_WITH_VALID_USERID =
      "testBlockUserSuccessWithValidUserId";

  public static final String TEST_GET_BLOCKED_USER_FAILURE_WITH_LOGINID =
      "testGetBlockedUserByLoginIdFailure";

  public static final String TEMPLATE_DIR = "templates/user/block";
  private static final String GET_USER_BY_ID_SERVER_URI = "/api/user/v1/read/";
  private static final String GET_USER_BY_ID_LOCAL_URI = "/v1/user/read/";

  private String getBlockUserUrl() {

    return getLmsApiUriPath("/api/user/v1/block", "/v1/user/block");
  }

  private String getUserByLoginIdUrl() {

    return getLmsApiUriPath("/api/user/v1/profile/read", "/v1/user/getuser");
  }

  @DataProvider(name = "blockUserFailureDataProvider")
  public Object[][] blockUserFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_BLOCK_USER_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED},
      new Object[] {TEST_BLOCK_USER_FAILURE_WITH_INVALID_USERID, true, HttpStatus.NOT_FOUND},
    };
  }

  @DataProvider(name = "blockUserSuccessDataProvider")
  public Object[][] blockUserSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_BLOCK_USER_SUCCESS_WITH_VALID_USERID, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "blockUserFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testBlockUserFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getBlockUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test(dataProvider = "blockUserSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testBlockUserSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    beforeTest();
    variable("userId", testContext.getVariable("userId"));
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getBlockUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  private void beforeTest() {
    UserUtil.getUserId(this, testContext);
  }

  @Test()
  @CitrusTest
  public void testGetBlockUserByUserIdFailure() {
    performGetTest(
        this,
        TEMPLATE_DIR,
        "testGetBlockedUserByUserIdFailure",
        getLmsApiUriPath(
            GET_USER_BY_ID_SERVER_URI,
            GET_USER_BY_ID_LOCAL_URI,
            TestActionUtil.getVariable(testContext, "userId")),
        true,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testGetUserByLoginIdFailure() {
    variable(
        "loginIdval", TestActionUtil.getVariable(testContext, "userName") + "@" + "channel_01");
    getAuthToken(this, true);
    performPostTest(
        this,
        TEMPLATE_DIR,
        TEST_GET_BLOCKED_USER_FAILURE_WITH_LOGINID,
        getUserByLoginIdUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }
}
