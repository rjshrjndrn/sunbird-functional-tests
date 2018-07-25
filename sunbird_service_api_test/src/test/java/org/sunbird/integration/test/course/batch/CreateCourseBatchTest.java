package org.sunbird.integration.test.course.batch;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.CourseUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateCourseBatchTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITHOUT_NAME =
      "testCreateCourseBatchFailureWithoutName";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITHOUT_COURSE_ID =
      "testCreateCourseBatchFailureWithoutCourseId";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITHOUT_ENROLLMENTTYPE =
      "testCreateCourseBatchFailureWithoutEnrollmentType";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_INVALID_ENROLLMENTTYPE =
      "testCreateCourseBatchFailureWithInvalidEnrollmentType";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITHOUT_START_DATE =
      "testCreateCourseBatchFailureWithoutStartDate";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_PAST_START_DATE =
      "testCreateCourseBatchFailurePastStartDate";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_INVALID_COURSE_ID =
      "testCreateCourseBatchFailureInvalidCourseId";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_PAST_END_DATE =
      "testCreateCourseBatchFailurePastEndDate";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_END_DATE_BEFORE_START_DATE =
      "testCreateCourseBatchFailureEndDateBeforeStartDate";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_INVALID_CREATED_FOR =
      "testCreateCourseBatchFailureInviteOnlyWithInvalidCreatedFor";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_INVALID_MENTOR =
      "testCreateCourseBatchFailureInviteOnlyWithInvalidMentor";

  public static final String TEST_NAME_CREATE_COURSE_BATCH_SUCCESS_INVITE_ONLY =
      "testCreateCourseBatchSuccessInviteOnly";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_SUCCESS_INVITE_ONLY_WITH_CREATED_FOR =
      "testCreateCourseBatchSuccessInviteOnlyWithCreatedFor";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_SUCCESS_INVITE_ONLY_WITH_MENTORS =
      "testCreateCourseBatchSuccessInviteOnlyWithMentors";
  public static final String TEST_NAME_CREATE_COURSE_BATCH_SUCCESS_OPEN =
      "testCreateCourseBatchSuccessOpen";

  public static final String TEMPLATE_DIR = "templates/course/batch/create";
  private static final String TODAY_DATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

  private String getCreateCourseBatchUrl() {
    return getLmsApiUriPath("/api/course/v1/batch/create", "/v1/course/batch/create");
  }

  @DataProvider(name = "createCourseBatchFailureDataProvider")
  public Object[][] createCourseBatchFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITHOUT_NAME, false, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITHOUT_COURSE_ID, false, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITHOUT_ENROLLMENTTYPE, false, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_INVALID_ENROLLMENTTYPE,
        false,
        HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITHOUT_START_DATE, false, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_PAST_START_DATE, false, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_INVALID_COURSE_ID, false, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_PAST_END_DATE, false, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_END_DATE_BEFORE_START_DATE,
        false,
        HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_INVALID_CREATED_FOR, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_COURSE_BATCH_FAILURE_WITH_INVALID_MENTOR, true, HttpStatus.BAD_REQUEST
      }
    };
  }

  @Test(dataProvider = "createCourseBatchFailureDataProvider")
  @CitrusParameters({"testName", "isCourseIdRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateCourseBatchFailure(
      String testName, boolean isCourseIdRequired, HttpStatus httpStatusCode) {
    beforeTest(isCourseIdRequired);
    getAuthToken(this, true);
    variable("startDate", TODAY_DATE);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateCourseBatchUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @DataProvider(name = "createCourseBatchSuccessDataProvider")
  public Object[][] createCourseBatchSuccessDataProvider() {
    return new Object[][] {
      new Object[] {TEST_NAME_CREATE_COURSE_BATCH_SUCCESS_INVITE_ONLY, HttpStatus.OK},
      new Object[] {TEST_NAME_CREATE_COURSE_BATCH_SUCCESS_OPEN, HttpStatus.OK}
    };
  }

  @Test(dataProvider = "createCourseBatchSuccessDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testCreateCourseBatchSuccess(String testName, HttpStatus httpStatusCode) {
    beforeTest(true);
    getAuthToken(this, true);
    variable("startDate", TODAY_DATE);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreateCourseBatchUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        true,
        httpStatusCode,
        RESPONSE_JSON);
  }

  public void beforeTest(boolean isCourseIdRequired) {
    if (isCourseIdRequired) {
      // courseUnitId is needed to be updated in context for creating course
      variable("courseUnitId", CourseUtil.getCourseUnitId());
      String courseId = CourseUtil.getCourseId(this, testContext);
      variable("courseId", courseId);
    }
  }
}
