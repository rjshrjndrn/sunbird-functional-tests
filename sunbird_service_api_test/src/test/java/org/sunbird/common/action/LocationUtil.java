package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;

public class LocationUtil {

  private static final String STATE_CODE =
      "FT_State_Code-" + String.valueOf(System.currentTimeMillis());
  private static final String DISTRICT_CODE =
      "FT_District_Code-" + String.valueOf(System.currentTimeMillis());

  public static void createLocation(TestNGCitrusTestRunner runner,
      TestContext testContext,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson,
      String contentType,
      boolean isAuthRequired,
      String locationType){

    if(Constant.LOCATION_TYPE_DISTRICT.equalsIgnoreCase(locationType)){
      runner.variable("locationCode", STATE_CODE);
      createLocation(runner, testContext, "templates/location/state/create/","testCreateLocationSuccess",requestUrl,
          requestJson , contentType, true, "state");
      runner.variable("parentId", testContext.getVariables().get(Constant.STATE_ID));
      runner.variable("locationCode", DISTRICT_CODE);
    }else{
      runner.variable("locationCode", STATE_CODE);
    }
    runner.http(builder -> TestActionUtil.getPostRequestTestAction(
        builder,
        Constant.LMS_ENDPOINT,
        templateDir,
        testName,
        requestUrl,
        requestJson,
        contentType,
        TestActionUtil.getHeaders(isAuthRequired)));
    if(Constant.LOCATION_TYPE_DISTRICT.equalsIgnoreCase(locationType)) {
      runner.http(builder -> TestActionUtil
          .getExtractFromResponseTestAction(testContext, builder, Constant.LMS_ENDPOINT,
              HttpStatus.OK, "$.result.id", Constant.DISTRICT_ID));
    }else if(Constant.LOCATION_TYPE_STATE.equalsIgnoreCase(locationType)){
      runner.http(builder -> TestActionUtil
          .getExtractFromResponseTestAction(testContext, builder, Constant.LMS_ENDPOINT,
              HttpStatus.OK, "$.result.id", Constant.STATE_ID));
    }
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);

  }

}
