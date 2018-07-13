package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchUserTest extends BaseCitrusTest {

  public static final String TEST_NAME_SEARCH_USER_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testSearchUserFailureWithoutAccessToken";

  public static final String TEMPLATE_DIR = "templates/user/search";

  private String getSearchUserUrl() {

    return getLmsApiUriPath("/api/user/v1/user/search", "/v1/user/search");
  }

  @DataProvider(name = "searchFailureUserDataProvider")
  public Object[][] searchFailureUserDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_SEARCH_USER_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
    };
  }

  @Test(dataProvider = "searchFailureUserDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchUserFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getSearchUserUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
