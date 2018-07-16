package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GetUserByUserIdTest extends BaseCitrusTest {
  public static final String TEMPLATE_DIR = "templates/user/getbyuserid";
  private static final String GET_USER_BY_ID_SERVER_URI = "/api/user/v1/read/";
  private static final String GET_USER_BY_ID_LOCAL_URI = "/v1/user/read/";

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
        testName,
        TEMPLATE_DIR,
        getLmsApiUriPath(
            GET_USER_BY_ID_SERVER_URI + pathParam, GET_USER_BY_ID_LOCAL_URI + pathParam),
        httpStatusCode,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON,
        matchResponseCodeOnly);
  }
}
