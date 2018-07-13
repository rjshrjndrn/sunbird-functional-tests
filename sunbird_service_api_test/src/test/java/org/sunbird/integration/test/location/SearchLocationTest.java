package org.sunbird.integration.test.location;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchLocationTest extends BaseCitrusTest {

  public static final String TEST_NAME_SEARCH_LOCATION_FAILURE_WITHOUT_FILTER =
      "testSearchLocationFailureWithoutFilter";
 


  public static final String TEMPLATE_DIR = "templates/location/search";

  private String getSearchLocationUrl() {

    return getLmsApiUriPath("/api/data/v1/location/search", "/v1/location/search");
  }

  @DataProvider(name = "searchFailureLocationDataProvider")
  public Object[][] searchFailureLocationDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_SEARCH_LOCATION_FAILURE_WITHOUT_FILTER,true,HttpStatus.BAD_REQUEST},
     
    };
  }

  @Test(dataProvider = "searchFailureLocationDataProvider")
  @CitrusParameters({"testName","isAuthRequired","httpStatusCode"})
  @CitrusTest
  public void testSearchLocationFailure(String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

     performPostTest(
        testName,
        TEMPLATE_DIR,
        getSearchLocationUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
