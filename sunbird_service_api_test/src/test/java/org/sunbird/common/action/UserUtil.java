package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;

public class UserUtil {

  public static String getCreateUserUrl(BaseCitrusTestRunner runner) {
    return runner.getLmsApiUriPath("/api/user/v1/create", "/v1/user/create");
  }

  public static final String TEMPLATE_DIR_USER_CREATE = "templates/user/create";
  public static final String TEMPLATE_DIR_USER_CREATE_TEST_CASE = "testCreateUserSuccess";

  private static String getBlockUserUrl(BaseCitrusTestRunner runner) {

    return runner.getLmsApiUriPath("/api/user/v1/block", "/v1/user/block");
  }

  private static String getUserProfileVisibilityUrl(BaseCitrusTestRunner runner) {
    return runner.getLmsApiUriPath(
        "/api/user/v1/profile/visibility", "/v1/user/profile/visibility");
  }

  public static void createUser(
      BaseCitrusTestRunner runner, TestContext testContext, String templateDir, String testName) {
    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                Constant.LMS_ENDPOINT,
                templateDir,
                testName,
                getCreateUserUrl(runner),
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                TestActionUtil.getHeaders(false)));

    runner.http(
        builder ->
            TestActionUtil.getExtractFromResponseTestAction(
                testContext,
                builder,
                Constant.LMS_ENDPOINT,
                HttpStatus.OK,
                "$.result.userId",
                "userId"));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  public static void blockUser(BaseCitrusTestRunner runner, String templateDir, String testName) {
    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                Constant.LMS_ENDPOINT,
                templateDir,
                testName,
                getBlockUserUrl(runner),
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                TestActionUtil.getHeaders(true)));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  public static void getUserId(BaseCitrusTestRunner runner, TestContext testContext) {
    if (StringUtils.isBlank((String) testContext.getVariables().get("userId"))) {
      String userName = Constant.USER_NAME_PREFIX + UUID.randomUUID().toString();
      testContext.setVariable("userName", userName);
      runner.variable("username", userName);
      UserUtil.createUser(
          runner, testContext, TEMPLATE_DIR_USER_CREATE, TEMPLATE_DIR_USER_CREATE_TEST_CASE);
    }
  }

  public static void setProfileVisibilityPrivate(
      BaseCitrusTestRunner runner, String templateDir, String testName) {
    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                Constant.LMS_ENDPOINT,
                templateDir,
                testName,
                getUserProfileVisibilityUrl(runner),
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                TestActionUtil.getHeaders(true)));
  }
}
