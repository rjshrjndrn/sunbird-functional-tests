package org.sunbird.integration.test.location.state;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.LocationUtil;
import org.sunbird.common.annotation.CleanUp;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StateLocationUpdateTest extends BaseCitrusTestRunner {

  private static final String STATE_CODE =
      "State-02-fuzzy-" + String.valueOf(System.currentTimeMillis());
  private static final String CREATE_LOCATION_SERVER_URI ="/api/data/v1/location/create";
  private static final String CREATE_LOCATION_LOCAL_URI = "/v1/location/create";
  private static final String UPDATE_LOCATION_SERVER_URI ="/api/data/v1/location/update";
  private static final String UPDATE_LOCATION_LOCAL_URI = "/v1/location/update";

  private static final String TEMPLATE_PATH = "templates/location/state/update";
  private static final String TEST_UPDATE_LOCATION_CODE_SUCCESS = "testUpdateLocationCodeSuccess";
  private static final String TEST_UPDATE_LOCATION_FAILURE_WITH_DUPLICATE_CODE = "testUpdateLocationFailureWithDuplicateCode";
  private static final String TEST_UPDATE_LOCATION_FAILURE_WITHOUT_REQUEST_BODY = "testUpdateLocationFailureWithoutRequestBody";
  private static final String TEST_UPDATE_LOCATION_NAME_SUCCESS = "testUpdateLocationNameSuccess";
  private static final String TEST_UPDATE_LOCATION_TYPE_FAILURE = "testUpdateLocationTypeFailure";

  private static final String LOCATION_ID = "locationId";

  @DataProvider(name = "createStateLocationDataProvider")
  public Object[][] createStateLocationDataProvider() {
    return new Object[][] {
        new Object[] {TEST_UPDATE_LOCATION_NAME_SUCCESS, true, HttpStatus.OK},
        new Object[] {TEST_UPDATE_LOCATION_CODE_SUCCESS, true, HttpStatus.OK},
        new Object[] {TEST_UPDATE_LOCATION_FAILURE_WITH_DUPLICATE_CODE, true, HttpStatus.BAD_REQUEST},
        new Object[] {TEST_UPDATE_LOCATION_FAILURE_WITHOUT_REQUEST_BODY, true, HttpStatus.BAD_REQUEST},
        new Object[] {TEST_UPDATE_LOCATION_TYPE_FAILURE, true, HttpStatus.BAD_REQUEST}
    };
  }

  @Test(dataProvider = "createStateLocationDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdateLocation(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    createStateLocation();
    variable("locationCode", STATE_CODE);
    variable(LOCATION_ID ,testContext.getVariables().get(Constant.STATE_ID));
    performPatchTest(
        this,
        TEMPLATE_PATH,
        testName,
        getUpdateLocationUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
    this.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  public void createStateLocation(){
    if(StringUtils.isBlank((String)testContext.getVariables().get(Constant.STATE_ID))) {
      variable("locationCode", STATE_CODE);
      LocationUtil.createLocation(this, testContext, "templates/location/state/create/",
          "testCreateLocationSuccess",
          getCreateLocationUrl(), REQUEST_JSON, MediaType.APPLICATION_JSON, true, Constant.LOCATION_TYPE_STATE);
    }
  }

  private static String getStateCode(){
    return "FT_State_Code-" + String.valueOf(System.currentTimeMillis());
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {
  }

  private String getCreateLocationUrl() {
    return getLmsApiUriPath(CREATE_LOCATION_SERVER_URI, CREATE_LOCATION_LOCAL_URI);
  }

  private String getUpdateLocationUrl() {
    return getLmsApiUriPath(UPDATE_LOCATION_SERVER_URI, UPDATE_LOCATION_LOCAL_URI);
  }
}
