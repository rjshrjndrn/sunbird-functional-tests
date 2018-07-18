package org.sunbird.integration.test.location;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchLocationTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_SEARCH_LOCATION_FAILURE_WITHOUT_FILTER =
      "testSearchLocationFailureWithoutFilter";

  public static final String TEMPLATE_DIR = "templates/location/search";

  private String getSearchLocationUrl() {

    return getLmsApiUriPath("/api/data/v1/location/search", "/v1/location/search");
  }

  @DataProvider(name = "searchLocationFailureDataProvider")
  public Object[][] searchLocationFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_SEARCH_LOCATION_FAILURE_WITHOUT_FILTER, HttpStatus.BAD_REQUEST},
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
}
