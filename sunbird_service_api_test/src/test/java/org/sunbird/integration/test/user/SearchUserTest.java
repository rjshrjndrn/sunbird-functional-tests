package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.common.action.UserUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchUserTest extends BaseCitrusTestRunner {

  public static final String BT_TEST_NAME_CREATE_USER_SUCCESS = "testCreateUserSuccessForSearch";
  public static final String BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS = "testCreateRootOrgSuccess";

  public static String UNIQUE_ID_GENERATION;
  public static String ROOT_ORG_CHANNAL;

  public static final String TEST_NAME_SEARCH_USER_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testSearchUserFailureWithoutAccessToken";
  public static final String TEST_NAME_SEARCH_USER_FAILURE_WITH_EMPTY_BODY =
      "testSearchUserFailureWithEmptyBody";

  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_FIRST_NAME =
      "testSearchUserSuccessWithFirstName";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_PHONE_NUMBER =
      "testSearchUserSuccessWithPhoneNumber";

  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_EMPTY_FILTER =
      "testSearchUserSuccessWithEmptyFilter";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_LIMIT =
      "testSearchUserSuccessWithLimit";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_UNKNOWN_FIELDS =
      "testSearchUserSuccessWithUnknownFields";

  public static final String TEMPLATE_DIR = "templates/user/search";
  public static final String BT_CREATE_USER_TEMPLATE_DIR = "templates/user/create";
  public static final String BT_ORG_CREATE_ORG_TEMPLATE_DIR = "templates/organisation/create";
  public static int count = 0;

  private String getSearchUserUrl() {
    return getLmsApiUriPath("/api/user/v1/search", "/v1/user/search");
  }

  public static String getCreateUserUrl(BaseCitrusTestRunner runner) {
    return runner.getLmsApiUriPath("/api/user/v1/create", "/v1/user/create");
  }

  @DataProvider(name = "searchUserFailureDataProvider")
  public Object[][] searchUserFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_SEARCH_USER_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED,
      },
      new Object[] {
        TEST_NAME_SEARCH_USER_FAILURE_WITH_EMPTY_BODY, true, HttpStatus.BAD_REQUEST,
      },
    };
  }

  @Test(dataProvider = "searchUserFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchUserFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @DataProvider(name = "searchUserSuccessDataProvider")
  public Object[][] searchUserSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_SEARCH_USER_SUCCESS_WITH_FIRST_NAME, true, HttpStatus.OK},
      new Object[] {TEST_NAME_SEARCH_USER_SUCCESS_WITH_PHONE_NUMBER, true, HttpStatus.OK},
      new Object[] {TEST_NAME_SEARCH_USER_SUCCESS_WITH_EMPTY_FILTER, true, HttpStatus.OK},
      new Object[] {TEST_NAME_SEARCH_USER_SUCCESS_WITH_LIMIT, true, HttpStatus.OK},
      new Object[] {
        TEST_NAME_SEARCH_USER_SUCCESS_WITH_UNKNOWN_FIELDS, true, HttpStatus.OK,
      },
    };
  }

  @Test(dataProvider = "searchUserSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchUserSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    beforeTestUserSearch(testName);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchUserUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  private void beforeTestUserSearch(String testName) {

    if (testName.equalsIgnoreCase(TEST_NAME_SEARCH_USER_SUCCESS_WITH_FIRST_NAME)
        || testName.equalsIgnoreCase(TEST_NAME_SEARCH_USER_SUCCESS_WITH_PHONE_NUMBER)) {
      if (count == 0) {
        count = 1;
        UNIQUE_ID_GENERATION = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        ROOT_ORG_CHANNAL = OrgUtil.getRootOrgChannel();
        variable("rootOrgChannel", ROOT_ORG_CHANNAL);
        variable("rootExternalId", OrgUtil.getRootOrgExternalId());
        OrgUtil.createOrg(
            this,
            testContext,
            BT_ORG_CREATE_ORG_TEMPLATE_DIR,
            BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS,
            HttpStatus.OK);

        variable("uniqueId", UNIQUE_ID_GENERATION);
        UserUtil.createUser(
            this, testContext, BT_CREATE_USER_TEMPLATE_DIR, BT_TEST_NAME_CREATE_USER_SUCCESS);
      } else {
        variable("uniqueId", UNIQUE_ID_GENERATION);
        variable("rootOrgChannel", ROOT_ORG_CHANNAL);
      }
    }
  }
}
