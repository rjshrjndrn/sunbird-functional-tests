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
        testName,
        TEMPLATE_DIR,
        getSearchOrgUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        false,
        MediaType.APPLICATION_JSON);
  }
}
