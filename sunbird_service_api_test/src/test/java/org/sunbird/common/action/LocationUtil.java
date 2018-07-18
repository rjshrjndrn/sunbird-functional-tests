package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;

public class LocationUtil {

  private static final String STATE_LOCATION_ID = "stateLocationId";
  private static final String STATE_CODE =
      "FT_State_Code-" + String.valueOf(System.currentTimeMillis());
  private static final String DISTRICT_CODE =
      "FT_District_Code-" + String.valueOf(System.currentTimeMillis());

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

    createStateLocation(runner, testContext, requestUrl);
    runner.variable("parentId", testContext.getVariables().get(STATE_LOCATION_ID));
    runner.variable("locationCode", DISTRICT_CODE);
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

  private static void createStateLocation(TestNGCitrusTestRunner runner,TestContext testContext, String requestUrl){
    if(StringUtils.isBlank((String)testContext.getVariables().get(STATE_LOCATION_ID))) {
      runner.variable("locationCode", STATE_CODE);
      createStateTypeLocation(runner, testContext, "templates/location/state/create/",
          "testCreateLocationSuccess",
          requestUrl, Constant.REQUEST_JSON, MediaType.APPLICATION_JSON, true, "$.result.id",STATE_LOCATION_ID);
    }
  }

}
