package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchIssuerTest extends BaseCitrusTest {

  public static final String TEST_NAME_SEARCH_ISSUER_FAILURE_WITHOUT_FILTER =
      "testSearchIssuerFailureWithoutFilter";

  public static final String TEMPLATE_DIR = "templates/badge/issuer/search";

  private String getSearchIssuerUrl() {

    return getLmsApiUriPath("/api/badging/v1/issuer/badge/search", "/v1/issuer/badge/search");
  }

  @DataProvider(name = "searchIssuerFailureDataProvider")
  public Object[][] searchIssuerFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_SEARCH_ISSUER_FAILURE_WITHOUT_FILTER, true, HttpStatus.BAD_REQUEST},
    };
  }

  @Test(dataProvider = "searchIssuerFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchIssuerFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getSearchIssuerUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
