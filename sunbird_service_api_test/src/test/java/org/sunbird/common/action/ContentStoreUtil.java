package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;

import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;

public class ContentStoreUtil {

  public static final String TEMPLATE_DIR = "templates/course/create";
  private static final String CONTENT_STORE_CREATE_URL = "/content/v3/create";
  private static final String CONTENT_STORE_UPDATE_HIERARCHY_URL = "/content/v3/hierarchy/update";
  private static final String CONTENT_STORE_CONTENT_PUBLISH_URL = "/content/v3/publish/";
  private static String courseId = null;
  private static final String courseUnitId = "SB_FT_COURSEUNIT_" + UUID.randomUUID().toString();
  private static final String resourceId = "do_1125535199417548801180";

  public static String getResourceId() {
    return resourceId;
  }

  public static String getCourseUnitId() {
    return courseUnitId;
  }

  public static String getCourseId(BaseCitrusTestRunner runner, TestContext testContext) {
    if (StringUtils.isBlank(courseId)) createLiveCourse(runner, testContext);
    return courseId;
  }

  private static Map<String, Object> getHeaders() {
    Map<String, Object> headers = TestActionUtil.getHeaders(false);
    headers.put(Constant.AUTHORIZATION, Constant.BEARER + System.getenv("ekstep_api_key"));
    return headers;
  }

  private static void createLiveCourse(BaseCitrusTestRunner runner, TestContext testContext) {
    createCourse(runner, testContext);

    updateCourseHierarchy(runner, testContext);

    publishCourse(runner, testContext);

    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  private static void createCourse(BaseCitrusTestRunner runner, TestContext testContext) {
    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                Constant.CONTENT_STORE_ENDPOINT,
                TEMPLATE_DIR,
                "testCreateCourseSuccess",
                CONTENT_STORE_CREATE_URL,
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                getHeaders()));
    runner.http(
        builder ->
            TestActionUtil.getExtractFromResponseTestAction(
                testContext,
                builder,
                Constant.CONTENT_STORE_ENDPOINT,
                HttpStatus.OK,
                "$.result.node_id",
                "courseId"));
    courseId = testContext.getVariable("courseId");
  }

  private static void updateCourseHierarchy(BaseCitrusTestRunner runner, TestContext testContext) {
    runner.http(
        builder ->
            TestActionUtil.getPatchRequestTestAction(
                builder,
                Constant.CONTENT_STORE_ENDPOINT,
                TEMPLATE_DIR,
                "testUpdateCourseHierarchySuccess",
                CONTENT_STORE_UPDATE_HIERARCHY_URL,
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                getHeaders()));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(
                builder,
                Constant.CONTENT_STORE_ENDPOINT,
                "testUpdateCourseSuccess",
                HttpStatus.OK));
  }

  private static void publishCourse(BaseCitrusTestRunner runner, TestContext testContext) {
    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                Constant.CONTENT_STORE_ENDPOINT,
                TEMPLATE_DIR,
                "testPublishCourseSuccess",
                CONTENT_STORE_CONTENT_PUBLISH_URL + courseId,
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                getHeaders()));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(
                builder,
                Constant.CONTENT_STORE_ENDPOINT,
                "testPublishCourseSuccess",
                HttpStatus.OK));
  }
}
