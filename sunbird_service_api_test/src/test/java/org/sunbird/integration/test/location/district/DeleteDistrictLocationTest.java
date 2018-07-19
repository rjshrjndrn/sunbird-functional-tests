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

public class DeleteDistrictLocationTest extends BaseCitrusTestRunner {

  private static final String CREATE_LOCATION_SERVER_URI = "/api/data/v1/location/create";
  private static final String CREATE_LOCATION_LOCAL_URI = "/v1/location/create";
  private static final String DELETE_LOCATION_SERVER_URI = "/api/data/v1/location/delete/";
  private static final String DELETE_LOCATION_LOCAL_URI = "/v1/location/delete/";

  private static final String TEMPLATE_PATH = "templates/location/district/delete";
  private static final String TEST_DELETE_LOCATION_SUCCESS = "testDeleteDistrictLocationSuccess";
  private static final String TEST_DELETE_LOCATION_FAILURE_WITHOUT_VALID_ID =
      "testDeleteDistrictLocationFailureWithoutValidId";

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
    createDistrictLocation();
    performDeleteTest(
        this,
        TEMPLATE_PATH,
        testName,
        getDeleteLocationUrl((String) testContext.getVariables().get(Constant.DISTRICT_ID)),
        null,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  public void createDistrictLocation() {
    if (StringUtils.isBlank((String) testContext.getVariables().get(Constant.DISTRICT_ID))) {
      LocationUtil.createDistrict(
          this,
          testContext,
          "templates/location/district/create/",
          "testCreateDistrictLocationSuccess",
          getCreateLocationUrl(),
          REQUEST_JSON);
    }
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {}

  private String getCreateLocationUrl() {
    return getLmsApiUriPath(CREATE_LOCATION_SERVER_URI, CREATE_LOCATION_LOCAL_URI);
  }

  private String getDeleteLocationUrl(String locationId) {
    return getLmsApiUriPath(DELETE_LOCATION_SERVER_URI, DELETE_LOCATION_LOCAL_URI, locationId);
  }
}
