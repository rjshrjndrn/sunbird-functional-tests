package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;

public class PageUtil {

  public static String getCreatePageUrl(BaseCitrusTestRunner runner) {
    return runner.getLmsApiUriPath("/api/data/v1/page/create", "/v1/page/create");
  }

  public static void createPage(
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
                getCreatePageUrl(runner),
                Constant.REQUEST_JSON,
                MediaType.APPLICATION_JSON.toString(),
                TestActionUtil.getHeaders(true)));
    runner.http(
        builder ->
            TestActionUtil.getExtractFromResponseTestAction(
                testContext,
                builder,
                Constant.LMS_ENDPOINT,
                responseCode,
                "$.result.pageId",
                "pageId"));

    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }
}
