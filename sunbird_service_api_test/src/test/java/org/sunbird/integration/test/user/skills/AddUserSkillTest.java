package org.sunbird.integration.test.user.skills;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddUserSkillTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_ADD_USER_SKILL_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testAddUserSkillFailureWithoutAccessToken";
  public static final String TEST_NAME_ADD_USER_SKILL_FAILURE_WITH_INVALID_USER_ID =
      "testAddUserSkillFailureWithInvalidUserId";
  public static final String TEST_NAME_ADD_USER_SKILL_FAILURE_WITHOUT_ENDORSED_USER_ID =
      "testAddUserSkillFailureWithoutEndorsedUserId";

  public static final String TEMPLATE_DIR = "templates/user/skill/add";

  private String getAddUserSkillUrl() {

    return getLmsApiUriPath("/api/user/v1/skill/add", "/v1/user/skill/add");
  }

  @DataProvider(name = "addUserSkillFailureDataProvider")
  public Object[][] addUserSkillFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_ADD_USER_SKILL_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {
        TEST_NAME_ADD_USER_SKILL_FAILURE_WITH_INVALID_USER_ID, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_ADD_USER_SKILL_FAILURE_WITHOUT_ENDORSED_USER_ID, true, HttpStatus.BAD_REQUEST
      },
    };
  }

  @Test(dataProvider = "addUserSkillFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testAddUserSkillFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getTestCase().setName(testName);
    getAuthToken(this, isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getAddUserSkillUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
