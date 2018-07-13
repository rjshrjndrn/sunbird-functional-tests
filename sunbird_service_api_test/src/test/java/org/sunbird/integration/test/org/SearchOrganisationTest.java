package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchOrganisationTest extends BaseCitrusTest {

  public static final String TEST_NAME_SEARCH_ORG_FAILURE_WITHOUT_FILTER =
      "testSearchOrgFailureWithoutFilter";

  public static final String TEMPLATE_DIR = "templates/organisation/search";

  private String getSearchOrgUrl() {

    return getLmsApiUriPath("/org/v1/search", "/v1/org/search");
  }

  @DataProvider(name = "searchFailureOrgDataProvider")
  public Object[][] searchFailureOrgDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_SEARCH_ORG_FAILURE_WITHOUT_FILTER, true, HttpStatus.INTERNAL_SERVER_ERROR
      },
    };
  }

  @Test(dataProvider = "searchFailureOrgDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchOrganisationFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getSearchOrgUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
