package org.sunbird.integration.test.location.state;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.annotation.CleanUp;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateStateLocationTest extends BaseCitrusTestRunner {

  private static final String STATE_CODE =
      "FT_State_Code-" + String.valueOf(System.currentTimeMillis());

  private static final String CREATE_LOCATION_SERVER_URI = "/api/data/v1/location/create";
  private static final String CREATE_LOCATION_LOCAL_URI = "/v1/location/create";

  private static final String TEMPLATE_PATH = "templates/location/state/create";
  private static final String TEST_CREATE_LOCATION_SUCCESS = "testCreateStateLocationSuccess";
  private static final String TEST_CREATE_LOCATION_FAILURE_DUPLICATE_CODE =
      "testCreateStateLocationFailureDuplicateCode";
  private static final String TEST_CREATE_LOCATION_FAILURE_WITHOUT_MANDATORY_PARAM =
      "testCreateStateLocationFailureWithoutMandatoryParam";
  private static final String TEST_CREATE_LOCATION_FAILURE_WITHOUT_REQUEST_BODY =
      "testCreateStateLocationFailureWithoutRequestBody";

  @DataProvider(name = "createStateLocationDataProvider")
  public Object[][] createStateLocationDataProvider() {
    return new Object[][] {
      new Object[] {TEST_CREATE_LOCATION_SUCCESS, true, HttpStatus.OK},
      new Object[] {TEST_CREATE_LOCATION_FAILURE_DUPLICATE_CODE, true, HttpStatus.BAD_REQUEST},
      new Object[] {
        TEST_CREATE_LOCATION_FAILURE_WITHOUT_MANDATORY_PARAM, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {TEST_CREATE_LOCATION_FAILURE_WITHOUT_REQUEST_BODY, true, HttpStatus.BAD_REQUEST}
    };
  }

  @Test(dataProvider = "createStateLocationDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreateStateLocation(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    variable("stateLocationCode", STATE_CODE);
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
    this.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {}

  private String getCreateLocationUrl() {
    return getLmsApiUriPath(CREATE_LOCATION_SERVER_URI, CREATE_LOCATION_LOCAL_URI);
  }
}
