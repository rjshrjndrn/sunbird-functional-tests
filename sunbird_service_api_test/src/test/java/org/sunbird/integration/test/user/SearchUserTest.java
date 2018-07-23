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
  public static final String TEST_NAME_SEARCH_USER_FAILURE_WITH_EMPTY_BODY =
      "testSearchUserFailureWithEmptyBody";

  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_FIRST_NAME =
      "testSearchUserSuccessWithFirstName";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_PHONE_NUMBER =
      "testSearchUserSuccessWithPhoneNumber";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_USER_EMAIL_ID =
      "testSearchUserSuccessWithUserEmailId";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_USER_NAME =
      "testSearchUserSuccessWithUserName";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_EMPTY_FILTER =
      "testSearchUserSuccessWithEmptyFilter";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_LIMIT =
      "testSearchUserSuccessWithLimit";
  public static final String TEST_NAME_SEARCH_USER_SUCCESS_WITH_UNKNOWN_FIELDS =
      "testSearchUserSuccessWithUnknownFields";

  public static final String TEMPLATE_DIR = "templates/user/search";

  private String getSearchUserUrl() {

    return getLmsApiUriPath("/api/user/v1/search", "/v1/user/search");
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
      new Object[] {TEST_NAME_SEARCH_USER_SUCCESS_WITH_USER_EMAIL_ID, true, HttpStatus.OK},
      new Object[] {TEST_NAME_SEARCH_USER_SUCCESS_WITH_USER_NAME, true, HttpStatus.OK},
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
