package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;

public class CourseUtil {

  public static final String TEMPLATE_DIR = "templates/course/create";
  private static final String EKSTEP_CONTENT_CREATE_URL = "/content/v3/create";
  private static final String EKSTEP_CONTENT_UPDATE_HIERARCHY_URL = "/content/v3/hierarchy/update";
  private static final String EKSTEP_CONTENT_PUBLISH_URL = "/content/v3/publish/";
  private static String courseId = null;
  private static final String courseUnitId = UUID.randomUUID().toString();
  public static final String RESPONSE_JSON = "response.json";

  public static String getCourseUnitId() {
    return courseUnitId;
  }

  public static String getCourseId(BaseCitrusTestRunner runner, TestContext testContext) {
    if (StringUtils.isBlank(courseId)) createCourseInEkstep(runner, testContext);
    return courseId;
  }

  private static void createCourseInEkstep(BaseCitrusTestRunner runner, TestContext testContext) {
    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                Constant.EKSTEP_ENDPOINT,
                TEMPLATE_DIR,
                "testCreateCourseSuccess",
                EKSTEP_CONTENT_CREATE_URL,
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                TestActionUtil.getHeaders(false)));
    runner.http(
        builder ->
            TestActionUtil.getExtractFromResponseTestAction(
                testContext,
                builder,
                Constant.EKSTEP_ENDPOINT,
                HttpStatus.OK,
                "$.result.node_id",
                "courseId"));
    courseId = testContext.getVariable("courseId");

    runner.http(
        builder ->
            TestActionUtil.getPatchRequestTestAction(
                builder,
                Constant.EKSTEP_ENDPOINT,
                TEMPLATE_DIR,
                "testUpdateCourseSuccess",
                EKSTEP_CONTENT_UPDATE_HIERARCHY_URL,
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                TestActionUtil.getHeaders(false)));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(
                builder, Constant.EKSTEP_ENDPOINT, "testUpdateCourseSuccess", HttpStatus.OK));

    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                Constant.EKSTEP_ENDPOINT,
                TEMPLATE_DIR,
                "testPublishCourseSuccess",
                EKSTEP_CONTENT_PUBLISH_URL + courseId,
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                TestActionUtil.getHeaders(false)));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(
                builder, Constant.EKSTEP_ENDPOINT, "testPublishCourseSuccess", HttpStatus.OK));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }
}
