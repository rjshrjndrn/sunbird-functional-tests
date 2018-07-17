package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GetUserByUserIdTest extends BaseCitrusTestRunner {
  public static final String TEMPLATE_DIR = "templates/user/getbyuserid";
  private static final String GET_USER_BY_ID_SERVER_URI = "/api/user/v1/read/";
  private static final String GET_USER_BY_ID_LOCAL_URI = "/v1/user/read/";

  @Autowired protected TestGlobalProperty config;
  @Autowired protected TestContext testContext;

  @DataProvider(name = "getUserByUserIdFailure")
  public Object[][] getUserByLoginIdFailure() {
    return new Object[][] {
      new Object[] {
        "testGetUserByUserIdFailureWithoutAuthToken",
        false,
        "4b981b53-f9eb-44fe",
        HttpStatus.UNAUTHORIZED,
        false
      },
      new Object[] {
        "testGetUserByUserIdFailureWithInvalidUserId",
        true,
        "4b981b53-f9eb-44fe",
        HttpStatus.BAD_REQUEST,
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
  public void testGetUserByLoginIdFailure(
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
}
