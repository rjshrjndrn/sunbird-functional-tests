package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;

public class UserUtil {

  public static String getCreateUserUrl(BaseCitrusTestRunner runner) {
    return runner.getLmsApiUriPath("/api/user/v1/create", "/v1/user/create");
  }

  public static void createUser(
      BaseCitrusTestRunner runner,
      TestContext testContext,
      String templateDir,
      String testName,
      HttpStatus responseCode) {
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
                responseCode,
                "$.result.userId",
                "userId"));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }
}
