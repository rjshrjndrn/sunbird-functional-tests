package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;

public class LocationUtil {

  private static final String STATE_CODE =
      "FT_State_Code-" + String.valueOf(System.currentTimeMillis());

  private static final String DISTRICT_CODE =
      "FT_District_Code-" + String.valueOf(System.currentTimeMillis());

  public static void createLocation(
      TestNGCitrusTestRunner runner,
      TestContext testContext,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson,
      boolean isAuthRequired,
      String locationType) {
    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                Constant.LMS_ENDPOINT,
                templateDir,
                testName,
                requestUrl,
                requestJson,
                MediaType.APPLICATION_JSON.toString(),
                TestActionUtil.getHeaders(isAuthRequired)));
    runner.http(
        builder ->
            TestActionUtil.getExtractFromResponseTestAction(
                testContext,
                builder,
                Constant.LMS_ENDPOINT,
                HttpStatus.OK,
                "$.result.id",
                locationType + "Id"));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  public static void createState(
      TestNGCitrusTestRunner runner,
      TestContext testContext,
      String requestUrl,
      String requestJson) {

    createLocation(
        runner,
        testContext,
        "templates/location/state/create/",
        "testCreateStateLocationSuccess",
        requestUrl,
        requestJson,
        true,
        Constant.LOCATION_TYPE_STATE);
  }

  public static void createDistrict(
      TestNGCitrusTestRunner runner,
      TestContext testContext,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson) {

    runner.variable("stateLocationCode", STATE_CODE);
    createState(runner, testContext, requestUrl, requestJson);
    runner.variable(
        "parentId", testContext.getVariables().get(Constant.LOCATION_TYPE_STATE + "Id"));
    runner.variable("districtLocationCode", DISTRICT_CODE);
    createLocation(
        runner,
        testContext,
        templateDir,
        testName,
        requestUrl,
        requestJson,
        true,
        Constant.LOCATION_TYPE_DISTRICT);
  }

  public static void createBlock(
      TestNGCitrusTestRunner runner,
      TestContext testContext,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson) {
    createDistrict(
        runner,
        testContext,
        "templates/location/district/create/",
        "testCreateDistrictLocationSuccess",
        requestUrl,
        requestJson);
    createLocation(
        runner,
        testContext,
        templateDir,
        testName,
        requestUrl,
        requestJson,
        true,
        Constant.LOCATION_TYPE_BLOCK);
  }
}
