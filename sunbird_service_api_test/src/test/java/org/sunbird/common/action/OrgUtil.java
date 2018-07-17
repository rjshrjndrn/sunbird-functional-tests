package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;

public class OrgUtil {

  public static String getCreateOrgUrl(BaseCitrusTestRunner runner) {
    return runner.getLmsApiUriPath("/api/org/v1/create", "/v1/org/create");
  }

  public static void createOrg(
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
                getCreateOrgUrl(runner),
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
                "$.result.organisationId",
                "organisationId"));
  }
}
