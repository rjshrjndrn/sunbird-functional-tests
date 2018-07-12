package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddUserToOrgTest extends BaseCitrusTest {

  public static final String TEST_ADD_USER_TO_ORG_FAILURE_WITH_EMPTY_ROLE_ARRAY =
      "testAddUserToOrgFailureWithEmptyRoleArray";

  public static final String TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_USER_ID =
      "testAddUserToOrgFailureWithInvalidUserId";
  public static final String TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_ORG_ID =
      "testAddUserToOrgFailureWithInvalidOrgId";

  public static final String TEMPLATE_DIR = "templates/organisation/user/add";

  @Autowired private TestGlobalProperty config;

  private String getCreateAddUserToOrgUrl() {
    return getLmsApiUriPath("/org/v1/member/add", "v1/org/member/add");
  }

  @DataProvider(name = "memeberAddToOrgFailureDataProvider")
  public Object[][] addUserToOrgFailureDataProvider() {

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

  @Test(dataProvider = "memeberAddToOrgFailureDataProvider")
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testAddUserToOrgFailure(String requestJson, String responseJson, String testName) {

    boolean isAuthRequired = true;

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getCreateAddUserToOrgUrl(),
        REQUEST_JSON,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
