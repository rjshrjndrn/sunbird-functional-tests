package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchOrganisationTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_SEARCH_ORG_FAILURE_WITHOUT_FILTER =
      "testSearchOrgFailureWithoutFilter";

  public static final String TEMPLATE_DIR = "templates/organisation/search";

  private String getSearchOrgUrl() {

    return getLmsApiUriPath("/api/org/v1/search", "/v1/org/search");
  }

  @DataProvider(name = "searchOrgFailureDataProvider")
  public Object[][] searchOrgFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_SEARCH_ORG_FAILURE_WITHOUT_FILTER, HttpStatus.INTERNAL_SERVER_ERROR},
    };
  }

  @Test(dataProvider = "searchOrgFailureDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testSearchOrganisationFailure(String testName, HttpStatus httpStatusCode) {
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchOrgUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
