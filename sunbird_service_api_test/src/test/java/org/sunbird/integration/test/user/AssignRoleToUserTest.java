package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.common.action.UserUtil;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AssignRoleToUserTest extends BaseCitrusTestRunner {
  private static final String TEST_ASSIGN_ROLE_USER_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testAssignRoleUserFailureWithoutAccessToken";
  private static final String TEST_ASSIGN_ROLE_USER_FAILURE_WITH_INVALID_USER_ID =
      "testAssignRoleUserFailureWithInvalidUserId";
  private static final String TEST_ASSIGN_ROLE_USER_FAILURE_WITH_INVALID_ORG_ID =
      "testAssignRoleUserFailureWithInvalidOrgId";

  private static final String TEST_ASSIGN_ROLE_USER_FAILURE_WITH_INVALID_ROLE =
      "testAssignRoleUserFailureWithInvalidRole";
  private static final String TEST_ASSIGN_ROLE_USER_FAILURE_WITH_USER_NOT_BELONG_TO_ORG =
      "testAssignRoleUserFailureWithUserNotBelongToOrg";
  private static final String TEST_ASSIGN_ROLE_USER_FAILURE_WITH_USER_ALREADY_BELONG_TO_ORG =
      "testAssignRoleUserFailureWithUserAlreadyBelongToOrg";

  public static final String TEMPLATE_DIR_USER_CREATE = "templates/user/create";
  public static final String TEMPLATE_DIR_USER_CREATE_TEST_CASE = "testCreateUserSuccess";

  public static final String TEMPLATE_DIR = "templates/user/role";

  public static final String BT_CREATE_ORG_TEMPLATE_DIR = "templates/organisation/create";
  public static final String BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS = "testCreateRootOrgSuccess";

  public static final String TEMPLATE_ORG_DIR = "templates/user/org";
  private static final String TEST_ASSIGN_USER_TO_ORG_SUCCESS = "testAssignUserToOrgSuccess";

  private String getAssignRoleToUserUrl() {
    return getLmsApiUriPath("/org/v1/role/assign", "/v1/user/assign/role");
  }

  @DataProvider(name = "AssignRoleToUserFailureDataProvider")
  public Object[][] assignRoleToUserFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_ASSIGN_ROLE_USER_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
    };
  }

  @Test(dataProvider = "AssignRoleToUserFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testAssignRoleToUserFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getAssignRoleToUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testAssignRoleToUserFailureWithInvalidUserId() {

    createOrg();
    variable("organisationId", testContext.getVariable("organisationId"));
    getAuthToken(this, true);
    performPostTest(
        this,
        TEMPLATE_DIR,
        TEST_ASSIGN_ROLE_USER_FAILURE_WITH_INVALID_USER_ID,
        getAssignRoleToUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testAssignRoleToUserFailureWithInvalidOrgId() {
    createUser();
    variable("organisationId", "#Invalid_Value");
    variable("userId", testContext.getVariable("userId"));
    getAuthToken(this, true);
    performPostTest(
        this,
        TEMPLATE_DIR,
        TEST_ASSIGN_ROLE_USER_FAILURE_WITH_INVALID_ORG_ID,
        getAssignRoleToUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testAssignRoleToUserFailureWithInvalidRole() {
    createOrg();
    createUser();
    variable("organisationId", testContext.getVariable("organisationId"));
    variable("userId", testContext.getVariable("userId"));
    getAuthToken(this, true);
    performPostTest(
        this,
        TEMPLATE_DIR,
        TEST_ASSIGN_ROLE_USER_FAILURE_WITH_INVALID_ROLE,
        getAssignRoleToUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testAssignRoleToUserFailureWithUserAlreadyBelongToOrg() {
    addUserToOrg();
    getAuthToken(this, true);
    performPostTest(
        this,
        TEMPLATE_DIR,
        TEST_ASSIGN_ROLE_USER_FAILURE_WITH_USER_ALREADY_BELONG_TO_ORG,
        getAssignRoleToUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.OK,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testAssignRoleToUserFailureWithUserNotBelongToOrg() {
    createOrg();
    createUser();
    variable("organisationId", testContext.getVariable("organisationId"));
    variable("userId", testContext.getVariable("userId"));
    getAuthToken(this, true);
    performPostTest(
        this,
        TEMPLATE_DIR,
        TEST_ASSIGN_ROLE_USER_FAILURE_WITH_USER_NOT_BELONG_TO_ORG,
        getAssignRoleToUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON);
  }

  private void createUser() {
    getAuthToken(this, true);
    String userName = Constant.USER_NAME_PREFIX + UUID.randomUUID().toString();
    testContext.setVariable("userName", userName);
    variable("username", userName);
    UserUtil.createUser(
        this,
        testContext,
        TEMPLATE_DIR_USER_CREATE,
        TEMPLATE_DIR_USER_CREATE_TEST_CASE,
        HttpStatus.OK,
        "$.result.userId",
        "userId");
  }

  private void createOrg() {
    getAuthToken(this, true);
    OrgUtil.createOrg(
        this,
        testContext,
        BT_CREATE_ORG_TEMPLATE_DIR,
        BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS,
        HttpStatus.OK);
  }

  private void addUserToOrg() {
    createOrg();
    createUser();
    variable("userId", testContext.getVariable("userId"));
    variable("organisationId", testContext.getVariable("organisationId"));
    OrgUtil.addUserToOrg(this, TEMPLATE_ORG_DIR, TEST_ASSIGN_USER_TO_ORG_SUCCESS);
  }
}
