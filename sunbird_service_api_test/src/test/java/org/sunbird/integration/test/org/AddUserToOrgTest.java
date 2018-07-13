package org.sunbird.integration.test.org;

import javax.ws.rs.core.MediaType;

import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;

public class AddUserToOrgTest extends BaseCitrusTest {

  public static final String TEST_ADD_USER_TO_ORG_FAILURE_WITH_EMPTY_ROLE_ARRAY =
      "testAddUserToOrgFailureWithEmptyRoleArray";

  public static final String TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_USER_ID =
      "testAddUserToOrgFailureWithInvalidUserId";
  public static final String TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_ORG_ID =
      "testAddUserToOrgFailureWithInvalidOrgId";

  public static final String TEMPLATE_DIR = "templates/organisation/user/add";

  private String getAddUserToOrgUrl() {
    return getLmsApiUriPath("/org/v1/member/add", "v1/org/member/add");
  }

  @DataProvider(name = "adduserToOrgFailureDataProvider")
  public Object[][] adduserToOrgFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_ADD_USER_TO_ORG_FAILURE_WITH_EMPTY_ROLE_ARRAY
      },
      new Object[] {
        TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_USER_ID
      },
      new Object[] {
        TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_ORG_ID
      },
    };
  }

  @Test(dataProvider = "adduserToOrgFailureDataProvider")
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testAddUserToOrgFailure(String testName) {

    boolean isAuthRequired = true;

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getAddUserToOrgUrl(),
        REQUEST_JSON,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
