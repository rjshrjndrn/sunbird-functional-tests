package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchUserTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_SEARCH_USER_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testSearchUserFailureWithoutAccessToken";
  public static final String TEST_SEARCH_USER_BY_FIRST_NAME_SUCCESS =
      "testSearchUserFirstNameSuccess";
  public static final String TEST_SEARCH_USER_WITH_QUERY_PARAM_SUCCESS =
      "testSearchUserWithQueryParamSuccess";
  public static final String TEST_SEARCH_USER_BY_PHONE_SUCCESS = "testSearchUserByPhoneSuccess";
  public static final String TEST_SEARCH_USER_BY_EMAIL_SUCCESS = "testSearchUserByEmailSuccess";
  public static final String TEST_SEARCH_USER_BY_USER_NAME_SUCCESS =
      "testSearchUserByUserNameSuccess";
  public static final String TEST_SEARCH_USER_BY_EMPTY_FILTER_SUCCESS =
      "testSearchUserByEmptyFilterSuccess";

  public static final String TEST_SEARCH_USER_BY_LIMIT_SUCCESS = "testSearchUserByLimitSuccess";

  public static final String TEST_SEARCH_USER_UNKNOWN_FIELDS_SUCCESS =
      "testSearchUserByUnknownFieldsSuccess";
  public static final String TEST_SEARCH_EMPTY_BODY_FAILURE = "testSearchUserByEmptyBodyFailure";

  public static final String TEMPLATE_DIR = "templates/user/search";

  private String getSearchUserUrl() {

    return getLmsApiUriPath("/api/user/v1/search", "/v1/user/search");
  }

  @DataProvider(name = "searchUserFailureDataProvider")
  public Object[][] searchUserFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_SEARCH_USER_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {TEST_SEARCH_EMPTY_BODY_FAILURE, true, HttpStatus.INTERNAL_SERVER_ERROR},
    };
  }

  @DataProvider(name = "searchUserSuccessDataProvider")
  public Object[][] searchUserSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_SEARCH_USER_BY_FIRST_NAME_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_SEARCH_USER_WITH_QUERY_PARAM_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_SEARCH_USER_BY_PHONE_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_SEARCH_USER_BY_EMAIL_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_SEARCH_USER_BY_USER_NAME_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_SEARCH_USER_BY_EMPTY_FILTER_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_SEARCH_USER_BY_LIMIT_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_SEARCH_USER_UNKNOWN_FIELDS_SUCCESS, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "searchUserFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchUserFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getTestCase().setName(testName);
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

  @Test(dataProvider = "searchUserSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchUserSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getTestCase().setName(testName);
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
}
