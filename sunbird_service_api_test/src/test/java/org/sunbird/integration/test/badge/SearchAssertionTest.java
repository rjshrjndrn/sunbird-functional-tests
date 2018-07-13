package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchAssertionTest extends BaseCitrusTest {

  public static final String TEST_NAME_SEARCH_ASSERTION_FAILURE_WITHOUT_FILTER =
      "testSearchAssertionFailureWithoutFilter";

  public static final String TEMPLATE_DIR = "templates/badge/assertion/search";

  private String getSearchIssuerUrl() {

    return getLmsApiUriPath(
        "/api/badging/v1/issuer/badge/assertion/search", "/v1/issuer/badge/assertion/search");
  }

  @DataProvider(name = "searchAssertionFailureDataProvider")
  public Object[][] searchAssertionFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_SEARCH_ASSERTION_FAILURE_WITHOUT_FILTER, HttpStatus.BAD_REQUEST},
    };
  }

  @Test(dataProvider = "searchAssertionFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchAssertionFailure(String testName, HttpStatus httpStatusCode) {

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getSearchIssuerUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        false,
        MediaType.APPLICATION_JSON);
  }
}
