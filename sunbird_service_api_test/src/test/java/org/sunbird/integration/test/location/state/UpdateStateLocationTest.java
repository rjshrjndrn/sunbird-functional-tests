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

public class UpdateStateLocationTest extends BaseCitrusTestRunner {

  private static final String STATE_CODE =
      "State-02-fuzzy-" + String.valueOf(System.currentTimeMillis());
  private static final String CREATE_LOCATION_SERVER_URI = "/api/data/v1/location/create";
  private static final String CREATE_LOCATION_LOCAL_URI = "/v1/location/create";
  private static final String UPDATE_LOCATION_SERVER_URI = "/api/data/v1/location/update";
  private static final String UPDATE_LOCATION_LOCAL_URI = "/v1/location/update";

  private static final String TEMPLATE_PATH = "templates/location/state/update";
  private static final String TEST_UPDATE_LOCATION_CODE_SUCCESS =
      "testUpdateStateLocationSuccessWithCode";
  private static final String TEST_UPDATE_LOCATION_FAILURE_WITH_DUPLICATE_CODE =
      "testUpdateStateLocationFailureWithDuplicateCode";
  private static final String TEST_UPDATE_LOCATION_FAILURE_WITHOUT_REQUEST_BODY =
      "testUpdateStateLocationFailureWithoutRequestBody";
  private static final String TEST_UPDATE_LOCATION_NAME_SUCCESS =
      "testUpdateStateLocationSuccessWithName";
  private static final String TEST_UPDATE_LOCATION_TYPE_FAILURE =
      "testUpdateStateLocationTypeFailure";

  private static final String LOCATION_ID = "locationId";

  @DataProvider(name = "createStateLocationDataProvider")
  public Object[][] createStateLocationDataProvider() {
    return new Object[][] {
      new Object[] {TEST_UPDATE_LOCATION_NAME_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_UPDATE_LOCATION_CODE_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_UPDATE_LOCATION_FAILURE_WITH_DUPLICATE_CODE, true, HttpStatus.BAD_REQUEST},
      new Object[] {
        TEST_UPDATE_LOCATION_FAILURE_WITHOUT_REQUEST_BODY, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {TEST_UPDATE_LOCATION_TYPE_FAILURE, true, HttpStatus.BAD_REQUEST}
    };
  }

  @Test(dataProvider = "createStateLocationDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testUpdateStateLocation(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    createStateLocation();
    variable("stateLocationCode", STATE_CODE);
    variable(LOCATION_ID, testContext.getVariables().get(Constant.STATE_ID));
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
  }

  public void createStateLocation() {
    if (StringUtils.isBlank((String) testContext.getVariables().get(Constant.STATE_ID))) {
      variable("stateLocationCode", STATE_CODE);
      LocationUtil.createState(this, testContext, getCreateLocationUrl(), REQUEST_JSON);
    }
  }

  private static String getStateCode() {
    return "FT_State_Code-" + String.valueOf(System.currentTimeMillis());
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {}

  private String getCreateLocationUrl() {
    return getLmsApiUriPath(CREATE_LOCATION_SERVER_URI, CREATE_LOCATION_LOCAL_URI);
  }

  private String getUpdateLocationUrl() {
    return getLmsApiUriPath(UPDATE_LOCATION_SERVER_URI, UPDATE_LOCATION_LOCAL_URI);
  }
}
