package org.sunbird.integration.test.user.notes;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.UserUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateUserNotesTest extends BaseCitrusTestRunner {
  private static final String TEST_CREATE_USER_NOTE_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testCreateUserNoteFailureWithoutAccessToken";

  private static final String TEST_CREATE_USER_NOTE_FAILURE_WITH_INVALID_USERID =
      "testCreateUserNoteFailureWithInvalidUserId";
  private static final String TEST_CREATE_USER_NOTE_FAILURE_WITHOUT_CONTENTID_OR_COURSEID =
      "testCreateUserNoteFailureWithoutContentIdOrCourseId";

  private static final String TEST_CREATE_USER_NOTE_FAILURE_WITHOUT_TITLE =
      "testCreateUserNoteFailureWithoutTitle";
  private static final String TEST_CREATE_USER_NOTE_FAILURE_WITHOUT_NOTE =
      "testCreateUserNoteFailureWithoutNote";
  private static final String TEST_CREATE_USER_NOTE_FAILURE_WITH_EMPTY_TAG =
      "testCreateUserNoteFailureWithEmptyTag";

  private static final String TEST_CREATE_USER_NOTE_SUCCESS_WITHOUT_TAG =
      "testCreateUserNoteSuccessWithoutTag";

  private static final String TEST_CREATE_USER_NOTE_SUCCESS_WITH_CONTENTID =
      "testCreateUserNoteSuccessWithContentId";

  private static final String TEST_CREATE_USER_NOTE_SUCCESS_WITH_COURSEID =
      "testCreateUserNoteSuccessWithCourseId";
  private static final String TEST_CREATE_USER_NOTE_SUCCESS_WITH_BOTH_COURSEID_AND_CONTENTID =
      "testCreateUserNoteSuccessWithCourseIdAndContentId";

  public static final String TEMPLATE_DIR = "templates/user/note/create";

  private String getCreateNoteUrl() {

    return getLmsApiUriPath("/api/note/v1/create", "/v1/note/create");
  }

  @DataProvider(name = "createUserNoteFailureDataProvider")
  public Object[][] createUserNoteFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_CREATE_USER_NOTE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {
        TEST_CREATE_USER_NOTE_FAILURE_WITH_INVALID_USERID, true, HttpStatus.UNAUTHORIZED
      },
      new Object[] {
        TEST_CREATE_USER_NOTE_FAILURE_WITHOUT_CONTENTID_OR_COURSEID, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {TEST_CREATE_USER_NOTE_FAILURE_WITHOUT_TITLE, true, HttpStatus.BAD_REQUEST},
      new Object[] {TEST_CREATE_USER_NOTE_FAILURE_WITHOUT_NOTE, true, HttpStatus.BAD_REQUEST},
      new Object[] {TEST_CREATE_USER_NOTE_FAILURE_WITH_EMPTY_TAG, true, HttpStatus.BAD_REQUEST},
    };
  }

  @DataProvider(name = "createUserNoteSuccessDataProvider")
  public Object[][] createUserNoteSuccessDataProvider() {
    return new Object[][] {
      new Object[] {TEST_CREATE_USER_NOTE_SUCCESS_WITHOUT_TAG, true, HttpStatus.OK},
      new Object[] {TEST_CREATE_USER_NOTE_SUCCESS_WITH_CONTENTID, true, HttpStatus.OK},
      new Object[] {TEST_CREATE_USER_NOTE_SUCCESS_WITH_COURSEID, true, HttpStatus.OK},
      new Object[] {
        TEST_CREATE_USER_NOTE_SUCCESS_WITH_BOTH_COURSEID_AND_CONTENTID, true, HttpStatus.OK
      },
    };
  }

  @Test(dataProvider = "createUserNoteFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateUserNoteFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    beforeTest(isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateNoteUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test(dataProvider = "createUserNoteSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateUserNoteSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    beforeTest(isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateNoteUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  void beforeTest(boolean isAuthRequired) {
    UserUtil.getUserId(this, testContext);
    String channelName = System.getenv("sunbird_default_channel");
    testContext.setVariable("password", "password");
    getAuthToken(
        this, isAuthRequired, testContext.getVariable("userName") + "@" + channelName, "password");
    variable("userId", testContext.getVariable("userId"));
  }
}
