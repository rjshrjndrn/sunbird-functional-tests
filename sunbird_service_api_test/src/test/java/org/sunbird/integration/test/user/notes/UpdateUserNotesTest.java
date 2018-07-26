package org.sunbird.integration.test.user.notes;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.UserUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UpdateUserNotesTest extends BaseCitrusTestRunner {

  private static final String TEST_UPDATE_USER_NOTE_FAILURE_WITH_INVALID_NOTEID =
      "testUpdateUserNoteFailureWithInvalidNoteId";
  private static final String TEST_UPDATE_USER_NOTE_FAILURE_WITH_INVALID_USERID =
      "testUpdateUserNoteFailureWithInvalidUserId";
  private static final String TEST_UPDATE_USER_NOTE_SUCCESS = "testUpdateUserNoteSuccess";
  public static final String TEMPLATE_DIR_CREATE = "templates/user/note/create";
  public static final String TEMPLATE_DIR = "templates/user/note/update";
  private static final String TEST_CREATE_USER_NOTE_SUCCESS_WITH_BOTH_COURSEID_AND_CONTENTID =
      "testCreateUserNoteSuccessWithCourseIdAndContentId";

  private String getUpdateNoteUrl(String pathParam) {
    return getLmsApiUriPath("/api/note/v1/update/", "/v1/note/update/", pathParam);
  }

  @DataProvider(name = "updateUserNoteFailureDataProvider")
  public Object[][] updateUserNoteFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_UPDATE_USER_NOTE_FAILURE_WITH_INVALID_USERID, true, HttpStatus.UNAUTHORIZED
      }
    };
  }

  @DataProvider(name = "updateUserNoteSuccessDataProvider")
  public Object[][] updateUserNoteSuccessDataProvider() {
    return new Object[][] {new Object[] {TEST_UPDATE_USER_NOTE_SUCCESS, true, HttpStatus.OK}};
  }

  @Test(dataProvider = "updateUserNoteFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdateUserNoteFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    beforeTest(isAuthRequired);
    getAuthToken(this, isAuthRequired);
    performPatchTest(
        this,
        TEMPLATE_DIR,
        testName,
        getUpdateNoteUrl(testContext.getVariable("noteId")),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test(dataProvider = "updateUserNoteSuccessDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdateUserNoteSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    beforeTest(isAuthRequired);
    performPatchTest(
        this,
        TEMPLATE_DIR,
        testName,
        getUpdateNoteUrl(testContext.getVariable("noteId")),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test()
  @CitrusTest
  public void testUpdateUserNoteFailureWithInvalidNoteId() {
    beforeTest(true);
    performPatchTest(
        this,
        TEMPLATE_DIR,
        TEST_UPDATE_USER_NOTE_FAILURE_WITH_INVALID_NOTEID,
        getUpdateNoteUrl("InvalidNoteId"),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        HttpStatus.UNAUTHORIZED,
        RESPONSE_JSON);
  }

  void beforeTest(boolean isAuthRequired) {
    UserUtil.getUserId(this, testContext);
    String channelName = System.getenv("sunbird_default_channel");
    getAuthToken(
        this, isAuthRequired, testContext.getVariable("userName") + "@" + channelName, "password");
    variable("userId", testContext.getVariable("userId"));
    UserUtil.createUserNote(
        this,
        testContext,
        TEMPLATE_DIR_CREATE,
        TEST_CREATE_USER_NOTE_SUCCESS_WITH_BOTH_COURSEID_AND_CONTENTID);
  }
}
