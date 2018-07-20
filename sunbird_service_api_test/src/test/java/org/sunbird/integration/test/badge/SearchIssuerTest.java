package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchIssuerTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_SEARCH_ISSUER_FAILURE_WITHOUT_FILTER =
      "testSearchIssuerFailureWithoutFilter";

  public static final String TEMPLATE_DIR = "templates/badge/issuer/search";

  private String getSearchIssuerUrl() {

    return getLmsApiUriPath("/api/badging/v1/issuer/badge/search", "/v1/issuer/badge/search");
  }

  @DataProvider(name = "searchIssuerFailureDataProvider")
  public Object[][] searchIssuerFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_SEARCH_ISSUER_FAILURE_WITHOUT_FILTER, HttpStatus.BAD_REQUEST},
    };
  }

  @Test(dataProvider = "searchIssuerFailureDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testSearchIssuerFailure(String testName, HttpStatus httpStatusCode) {

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchIssuerUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
