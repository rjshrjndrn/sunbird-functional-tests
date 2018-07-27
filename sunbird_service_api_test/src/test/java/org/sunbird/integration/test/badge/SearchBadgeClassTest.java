package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchBadgeClassTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_SEARCH_BADGE_CLASS_FAILURE_WITHOUT_FILTER =
      "testSearchBadgeClassFailureWithoutFilter";

  public static final String TEMPLATE_DIR = "templates/badge/class/search";

  private String getSearchBadgeClassUrl() {
    return getLmsApiUriPath("/api/badging/v1/issuer/badge/search", "/v1/issuer/badge/search");
  }

  @DataProvider(name = "searchBadgeClassFailureDataProvider")
  public Object[][] searchBadgeClassFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_SEARCH_BADGE_CLASS_FAILURE_WITHOUT_FILTER, HttpStatus.BAD_REQUEST},
    };
  }

  @Test(dataProvider = "searchBadgeClassFailureDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testSearchBadgeClassFailure(String testName, HttpStatus httpStatusCode) {

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName, getSearchBadgeClassUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
