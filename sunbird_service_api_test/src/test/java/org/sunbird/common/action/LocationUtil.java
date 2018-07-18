package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;

public class LocationUtil {

  public static void createDistrictTypeLocation(
      TestNGCitrusTestRunner runner,
      TestContext testContext,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson,
      String contentType,
      boolean isAuthRequired,
      String extractedValue,
      String extractedValueTo) {

    runner.http(builder -> TestActionUtil.getPostRequestTestAction(
        builder,
        Constant.LMS_ENDPOINT,
        templateDir,
        testName,
        requestUrl,
        requestJson,
        contentType,
        TestActionUtil.getHeaders(isAuthRequired)));
    runner.http(builder-> TestActionUtil.getExtractFromResponseTestAction(testContext, builder, Constant.LMS_ENDPOINT, HttpStatus.OK, extractedValue, extractedValueTo));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);

  }

  public static void createStateTypeLocation(
      TestNGCitrusTestRunner runner,
      TestContext testContext,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson,
      String contentType,
      boolean isAuthRequired,
      String extractedValue,
      String extractedValueTo) {

    runner.http(builder -> TestActionUtil.getPostRequestTestAction(
        builder,
        Constant.LMS_ENDPOINT,
        templateDir,
        testName,
        requestUrl,
        requestJson,
        contentType,
        TestActionUtil.getHeaders(isAuthRequired)));
    runner.http(builder-> TestActionUtil.getExtractFromResponseTestAction(testContext, builder, Constant.LMS_ENDPOINT, HttpStatus.OK, extractedValue, extractedValueTo));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);

  }

}
