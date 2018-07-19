package org.sunbird.integration.test.location;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.LocationUtil;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchLocationTest extends BaseCitrusTestRunner {

  public static final String TEST_SEARCH_LOCATION_FAILURE_WITHOUT_FILTER =
      "testSearchLocationFailureWithoutFilter";
  public static final String TEST_SEARCH_LOCATION_FAILURE_WITHOUT_REQUEST_BODY =
      "testSearchLocationFailureWithoutRequestBody";
  public static final String TEST_SEARCH_LOCATION_SUCCESS =
      "testSearchLocationSuccess";
  private static final String CREATE_LOCATION_SERVER_URI = "/api/data/v1/location/create";
  private static final String CREATE_LOCATION_LOCAL_URI = "/v1/location/create";

  public static final String TEMPLATE_DIR = "templates/location/search";
  private static final String STATE_CODE =
      "FT_State_Code-" + String.valueOf(System.currentTimeMillis());

  @DataProvider(name = "searchLocationFailureDataProvider")
  public Object[][] searchLocationFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_SEARCH_LOCATION_FAILURE_WITHOUT_FILTER, HttpStatus.BAD_REQUEST},
        new Object[] {TEST_SEARCH_LOCATION_FAILURE_WITHOUT_REQUEST_BODY, HttpStatus.BAD_REQUEST}
    };
  }

  @DataProvider(name = "searchLocationSuccessDataProvider")
  public Object[][] searchLocationSuccessDataProvider() {

    return new Object[][] {
        new Object[] {TEST_SEARCH_LOCATION_SUCCESS, HttpStatus.OK}
    };
  }

  @Test(dataProvider = "searchLocationFailureDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testSearchLocationFailure(String testName, HttpStatus httpStatusCode) {

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchLocationUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @Test(dataProvider = "searchLocationSuccessDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testSearchLocationSuccess(String testName, HttpStatus httpStatusCode) {

    getAuthToken(this, true);
    variable("stateLocationCode", STATE_CODE);
    createStateLocation();
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchLocationUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        httpStatusCode,
        RESPONSE_JSON);
  }

  public void createStateLocation() {
    if (StringUtils.isBlank((String) testContext.getVariables().get(Constant.STATE_ID))) {
      variable("stateLocationCode", STATE_CODE);
      LocationUtil.createState(this, testContext, getCreateLocationUrl(), REQUEST_JSON);
    }
  }

  private String getCreateLocationUrl() {
    return getLmsApiUriPath(CREATE_LOCATION_SERVER_URI, CREATE_LOCATION_LOCAL_URI);
  }

  private String getSearchLocationUrl() {
    return getLmsApiUriPath("/api/data/v1/location/search", "/v1/location/search");
  }

}
