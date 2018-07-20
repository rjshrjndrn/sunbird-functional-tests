package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.TestActionUtil;
import org.sunbird.common.action.UserUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GetUserByUserIdTest extends BaseCitrusTestRunner {
  public static final String TEMPLATE_DIR = "templates/user/getbyuserid";
  private static final String GET_USER_BY_ID_SERVER_URI = "/api/user/v1/read/";
  private static final String GET_USER_BY_ID_LOCAL_URI = "/v1/user/read/";
  public static final String TEMPLATE_DIR_BLOCK = "templates/user/block";
  public static final String TEST_BA_BLOCK_USER_SUCCESS_WITH_VALID_USERID =
      "testBlockUserSuccessWithValidUserId";

  @DataProvider(name = "getUserByUserIdFailure")
  public Object[][] getUserByUserIdFailure() {
    return new Object[][] {
      new Object[] {
        "testGetUserByUserIdFailureWithoutAuthToken",
        false,
        "invalidUserId",
        HttpStatus.UNAUTHORIZED
      },
      new Object[] {
        "testGetUserByUserIdFailureWithInvalidUserId",
        true,
        "4b981b53-f9eb-44fe",
        HttpStatus.BAD_REQUEST
      },
      new Object[] {"testGetUserByUserIdFailureWithEmptyUserId", true, "", HttpStatus.NOT_FOUND}
    };
  }

  @Test(dataProvider = "getUserByUserIdFailure")
  @CitrusParameters({"testName", "isAuthRequired", "pathParam", "httpStatusCode"})
  @CitrusTest
  public void testGetUserByUserIdFailure(
      String testName, boolean isAuthRequired, String pathParam, HttpStatus httpStatusCode) {
    performGetTest(
        this,
        TEMPLATE_DIR,
        testName,
        getLmsApiUriPath(GET_USER_BY_ID_SERVER_URI, GET_USER_BY_ID_LOCAL_URI, pathParam),
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testGetUserByUserIdSuccess() {
    beforeTest();
    performGetTest(
        this,
        TEMPLATE_DIR,
        "testGetUserByUserIdSuccess",
        getLmsApiUriPath(
            GET_USER_BY_ID_SERVER_URI,
            GET_USER_BY_ID_LOCAL_URI,
            TestActionUtil.getVariable(testContext, "userId")),
        true,
        HttpStatus.OK,
        RESPONSE_JSON);
    afterTest();
  }

  @Test()
  @CitrusTest
  public void testGetBlockUserByUserIdFailure() {
    getAuthToken(this, true);
    beforeTest();
    blockUser();
    performGetTest(
        this,
        TEMPLATE_DIR,
        "testGetUserByUserIdFailureWithBlockedUser",
        getLmsApiUriPath(
            GET_USER_BY_ID_SERVER_URI,
            GET_USER_BY_ID_LOCAL_URI,
            TestActionUtil.getVariable(testContext, "userId")),
        true,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  private void blockUser() {
    UserUtil.blockUser(this, TEMPLATE_DIR_BLOCK, TEST_BA_BLOCK_USER_SUCCESS_WITH_VALID_USERID);
  }

  private void beforeTest() {
    UserUtil.createUser(this, testContext, TEMPLATE_DIR, "testGetUserByUserIdSuccess");
  }

  private void afterTest() {}
}
