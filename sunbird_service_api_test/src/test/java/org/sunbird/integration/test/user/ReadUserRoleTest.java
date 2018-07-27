package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReadUserRoleTest extends BaseCitrusTestRunner {

  public static final String TEST_READ_USER_ROLE_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testReadUserRoleFailureWithoutAccessToken";

  public static final String TEST_READ_USER_ROLE_SUCCESS_WITH_ACCESS_TOKEN =
      "testReadUserRoleSuccessWithAccessToken";

  public static final String TEMPLATE_DIR = "templates/user/read/role";

  private String getReadUserRoleUrl() {
    return getLmsApiUriPath("/api/user/v1/role/read", "/v1/role/read");
  }

  @DataProvider(name = "readUserRoleFailureDataProvider")
  public Object[][] readUserRoleFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_READ_USER_ROLE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      }
    };
  }

  @DataProvider(name = "readUserRoleSuccessDataProvider")
  public Object[][] readUserRoleSuccessDataProvider() {
    return new Object[][] {
      new Object[] {TEST_READ_USER_ROLE_SUCCESS_WITH_ACCESS_TOKEN, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "readUserRoleFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testReadUserRoleFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getTestCase().setName(testName);
    getAuthToken(this, isAuthRequired);
    performGetTest(
        this,
        TEMPLATE_DIR,
        testName,
        getReadUserRoleUrl(),
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test(dataProvider = "readUserRoleSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testReadUserRoleSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getTestCase().setName(testName);
    getAuthToken(this, isAuthRequired);
    performGetTest(
        this,
        TEMPLATE_DIR,
        testName,
        getReadUserRoleUrl(),
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
