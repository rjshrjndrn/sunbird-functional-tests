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

public class CreateDistrictLocationTest extends BaseCitrusTestRunner {

  private static final String DISTRICT_CODE =
      "FT_District_Code-" + String.valueOf(System.currentTimeMillis());
  private static final String CREATE_LOCATION_SERVER_URI = "/api/data/v1/location/create";
  private static final String CREATE_LOCATION_LOCAL_URI = "/v1/location/create";
  private static final String PARENT_ID = "parentId";

  private static final String TEMPLATE_PATH = "templates/location/district/create/";
  private static final String TEST_CREATE_LOCATION_SUCCESS = "testCreateDistrictLocationSuccess";
  private static final String TEST_CREATE_LOCATION_FAILURE_DUPLICATE_CODE =
      "testCreateDistrictLocationFailureDuplicateCode";
  private static final String TEST_CREATE_LOCATION_FAILURE_WITHOUT_PARENT_ID =
      "testCreateDistrictLocationFailureWithoutParentId";

  @DataProvider(name = "createLocationDataProvider")
  public Object[][] createLocationDataProvider() {
    return new Object[][] {
      new Object[] {TEST_CREATE_LOCATION_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_CREATE_LOCATION_FAILURE_DUPLICATE_CODE, true, HttpStatus.BAD_REQUEST},
      new Object[] {TEST_CREATE_LOCATION_FAILURE_WITHOUT_PARENT_ID, true, HttpStatus.BAD_REQUEST}
    };
  }

  @Test(dataProvider = "createLocationDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateLocation(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    createStateLocation();
    variable(PARENT_ID, testContext.getVariables().get(Constant.STATE_ID));
    variable("districtLocationCode", DISTRICT_CODE);
    performPostTest(
        this,
        TEMPLATE_PATH,
        testName,
        getCreateLocationUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  public void createStateLocation() {
    if (StringUtils.isBlank((String) testContext.getVariables().get(Constant.STATE_ID))) {
      variable("stateLocationCode", getStateCode());
      LocationUtil.createState(this, testContext, getCreateLocationUrl(), REQUEST_JSON);
    }
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {}

  private String getCreateLocationUrl() {
    return getLmsApiUriPath(CREATE_LOCATION_SERVER_URI, CREATE_LOCATION_LOCAL_URI);
  }

  private static String getStateCode() {
    return "FT_State_Code-" + String.valueOf(System.currentTimeMillis());
  }
}
