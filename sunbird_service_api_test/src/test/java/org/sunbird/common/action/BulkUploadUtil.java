package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

public class BulkUploadUtil {

  private static final String BULK_UPLOAD_ORGANISATION_SERVER_URI="/api/org/v1/upload";
  private static final String BULK_UPLOAD_ORGANISATION_LOCAL_URI ="/v1/org/upload";

  public static String getOrgBulkUploadUrl(BaseCitrusTestRunner runner) {
    return runner.getLmsApiUriPath(BULK_UPLOAD_ORGANISATION_SERVER_URI, BULK_UPLOAD_ORGANISATION_LOCAL_URI);
  }

  public static void orgBulkUpload(
      BaseCitrusTestRunner runner,
      TestContext testContext,
      String templateDir,
      String testName,
      HttpStatus responseCode,
      TestGlobalProperty config) {
    runner.http(
        builder ->
            TestActionUtil.getMultipartRequestTestAction(
                testContext,
                builder,
                Constant.LMS_ENDPOINT,
                templateDir,
                testName,
                getOrgBulkUploadUrl(runner),
                Constant.REQUEST_FORM_DATA,
                TestActionUtil.getHeaders(true),
                runner.getClass().getClassLoader(),
                config));
    runner.http(
        builder ->
            TestActionUtil.getExtractFromResponseTestAction(
                testContext,
                builder,
                Constant.LMS_ENDPOINT,
                responseCode,
                "$.result.processId",
                Constant.BULK_UPLOAD_PROCESS_ID));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

}
