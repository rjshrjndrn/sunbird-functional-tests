package org.sunbird.integration.test.location.district;

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

public class DistrictLocationDeleteTest extends BaseCitrusTestRunner {

  private static final String STATE_CODE =
      "State-02-fuzzy-" + String.valueOf(System.currentTimeMillis());
  private static final String DISTRICT_CODE =
      "District-02-fuzzy-" + String.valueOf(System.currentTimeMillis());
  private static final String CREATE_LOCATION_SERVER_URI ="/api/data/v1/location/create";
  private static final String CREATE_LOCATION_LOCAL_URI = "/v1/location/create";
  private static final String DELETE_LOCATION_SERVER_URI ="/api/data/v1/location/delete";
  private static final String DELETE_LOCATION_LOCAL_URI = "/v1/location/delete";

  private static final String TEMPLATE_PATH = "templates/location/district/delete";
  private static final String TEST_DELETE_LOCATION_SUCCESS = "testDeleteLocationSuccess";
  private static final String TEST_DELETE_LOCATION_FAILURE_WITHOUT_VALID_ID = "testDeleteLocationFailureWithoutValidId";

  private static final String STATE_LOCATION_ID = "stateLocationId";
  private static final String DISTRICT_LOCATION_ID = "districtLocationId";

  @DataProvider(name = "deleteLocationDataProvider")
  public Object[][] deleteLocationDataProvider() {
    return new Object[][] {
        new Object[] {TEST_DELETE_LOCATION_SUCCESS, true, HttpStatus.OK},
        new Object[] {TEST_DELETE_LOCATION_FAILURE_WITHOUT_VALID_ID, true, HttpStatus.BAD_REQUEST}
    };
  }

  @Test(dataProvider = "deleteLocationDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testDeleteLocation(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    createStateLocation();
    createDistrictLocation();
    performDeleteTest(
        this,
        TEMPLATE_PATH,
        testName,
        getDeleteLocationUrl((String)testContext.getVariables().get(DISTRICT_LOCATION_ID)),
        null,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
    this.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  public void createStateLocation(){
    if(StringUtils.isBlank((String)testContext.getVariables().get(STATE_LOCATION_ID))) {
      variable("locationCode", STATE_CODE);
      LocationUtil.createStateTypeLocation(this, testContext, "templates/location/state/create/",
          "testCreateLocationSuccess",
          getCreateLocationUrl(), REQUEST_JSON, MediaType.APPLICATION_JSON, true, "$.result.id",STATE_LOCATION_ID);
    }
  }

  public void createDistrictLocation(){
    if(StringUtils.isBlank((String)testContext.getVariables().get(DISTRICT_LOCATION_ID))) {
      variable("parentId", testContext.getVariables().get(STATE_LOCATION_ID));
      variable("locationCode", DISTRICT_CODE);
      LocationUtil.createDistrictTypeLocation(this, testContext, "templates/location/district/create/",
          "testCreateLocationSuccess",
          getCreateLocationUrl(), REQUEST_JSON, MediaType.APPLICATION_JSON, true, "$.result.id",DISTRICT_LOCATION_ID);
    }
  }

  private static String getStateCode(){
    return "State-02-fuzzy-" + String.valueOf(System.currentTimeMillis());
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {
  }

  private String getCreateLocationUrl() {
    return getLmsApiUriPath(CREATE_LOCATION_SERVER_URI, CREATE_LOCATION_LOCAL_URI);
  }

  private String getDeleteLocationUrl(String locationId) {
    return (getLmsApiUriPath(DELETE_LOCATION_SERVER_URI, DELETE_LOCATION_LOCAL_URI)+"/"+locationId);
  }
}
