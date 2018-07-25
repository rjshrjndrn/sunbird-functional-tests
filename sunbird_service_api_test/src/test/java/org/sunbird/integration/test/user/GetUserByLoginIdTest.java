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

public class GetUserByLoginIdTest extends BaseCitrusTestRunner {
  public static final String TEMPLATE_DIR = "templates/user/getbyloginid";
  private static final String GET_USER_BY_ID_SERVER_URI = "/api/user/v1/profile/read";
  private static final String GET_USER_BY_ID_LOCAL_URI = "/v1/user/getuser";
  public static final String TEST_GET_USER_BY_LOGIN_ID_FAILURE_WITH_BLOCKED_USER =
      "testGetUserByLoginIdFailureWithBlockedUser";
  public static final String TEMPLATE_DIR_BLOCK = "templates/user/block";
  public static final String TEST_BA_BLOCK_USER_SUCCESS_WITH_VALID_USERID =
      "testBlockUserSuccessWithValidUserId";

  @DataProvider(name = "getUserByLoginIdFailure")
  public Object[][] getUserByLoginIdFailure() {
    return new Object[][] {
      new Object[] {"testGetUserByLoginIdFailureWithInvalidLoginId"},
      new Object[] {"testGetUserByLoginIdFailureWithEmptyLoginId"},
      new Object[] {"testGetUserByLoginIdFailureWithoutLoginId"}
    };
  }

  @Test(dataProvider = "getUserByLoginIdFailure")
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testGetUserByLoginIdFailure(String testName) {
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getLmsApiUriPath(GET_USER_BY_ID_SERVER_URI, GET_USER_BY_ID_LOCAL_URI),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testGetUserByLoginIdFailureForBlockUser() {
    getAuthToken(this, true);
    beforeTest();
    variable(
        "loginIdval", TestActionUtil.getVariable(testContext, "userName") + "@" + "channel_01");
    performPostTest(
        this,
        TEMPLATE_DIR,
        TEST_GET_USER_BY_LOGIN_ID_FAILURE_WITH_BLOCKED_USER,
        getLmsApiUriPath(GET_USER_BY_ID_SERVER_URI, GET_USER_BY_ID_LOCAL_URI),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  private void beforeTest() {
    UserUtil.getUserId(this, testContext);
    UserUtil.blockUser(this, TEMPLATE_DIR_BLOCK, TEST_BA_BLOCK_USER_SUCCESS_WITH_VALID_USERID);
  }
}
