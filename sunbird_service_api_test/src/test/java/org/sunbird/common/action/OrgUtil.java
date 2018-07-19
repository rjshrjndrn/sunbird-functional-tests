package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;

public class OrgUtil {

  private static String rootOrgId = null;

  private static final String rootChannel = "FT_Org_Channel_" + (new Random()).nextInt(100);
  private static final String rootExternalId = "FT_Org_External_" + (new Random()).nextInt(100);

  public static String getRootChannel() {
    return rootChannel;
  }

  public static String getRootExternalId() {
    return rootExternalId;
  }

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
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  public static void getRootOrgId(BaseCitrusTestRunner runner, TestContext testContext) {
    if (StringUtils.isBlank(rootOrgId)) {
      createOrg(
          runner,
          testContext,
          "templates/organisation/create",
          "testCreateRootOrgSuccess",
          HttpStatus.OK);
      rootOrgId = testContext.getVariable("organisationId");
    } else {
      testContext.setVariable("organisationId", rootOrgId);
    }
  }
}
