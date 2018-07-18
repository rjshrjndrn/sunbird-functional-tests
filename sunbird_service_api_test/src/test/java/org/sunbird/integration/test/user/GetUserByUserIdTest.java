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

  @DataProvider(name = "getUserByUserIdFailure")
  public Object[][] getUserByUserIdFailure() {
    return new Object[][] {
      new Object[] {
        "testGetUserByUserIdFailureWithoutAuthToken",
        false,
        "invalidUserId",
        HttpStatus.UNAUTHORIZED,
        false
      },
      new Object[] {
        "testGetUserByUserIdFailureWithInvalidUserId",
        true,
        "4b981b53-f9eb-44fe",
        HttpStatus.NOT_FOUND,
        false
      },
      new Object[] {
        "testGetUserByUserIdFailureWithEmptyUserId", true, "", HttpStatus.NOT_FOUND, true
      }
    };
  }

  @Test(dataProvider = "getUserByUserIdFailure")
  @CitrusParameters({
    "testName",
    "isAuthRequired",
    "pathParam",
    "httpStatusCode",
    "matchResponseCodeOnly"
  })
  @CitrusTest
  public void testGetUserByIdFailure(
      String testName,
      boolean isAuthRequired,
      String pathParam,
      HttpStatus httpStatusCode,
      boolean matchResponseCodeOnly) {
    performGetTest(
        this,
        TEMPLATE_DIR,
        testName,
        getLmsApiUriPath(
            GET_USER_BY_ID_SERVER_URI + pathParam, GET_USER_BY_ID_LOCAL_URI + pathParam),
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON,
        matchResponseCodeOnly);
  }

  @Test()
  @CitrusTest
  public void testGetUserByIdSuccess() {
    beforeTest();
    String userId = TestActionUtil.getVariable(testContext, "userId");
    System.out.println("User Id found==" + userId);
    performGetTest(
        this,
        TEMPLATE_DIR,
        "testGetUserByUserIdSuccess",
        getLmsApiUriPath(GET_USER_BY_ID_SERVER_URI + userId, GET_USER_BY_ID_LOCAL_URI + userId),
        true,
        HttpStatus.OK,
        RESPONSE_JSON,
        false);
    afterTest();
  }

  private void beforeTest() {
    UserUtil.createUser(
        this, testContext, TEMPLATE_DIR, "testGetUserByUserIdSuccess", HttpStatus.OK);
  }

  private void afterTest() {}
}
