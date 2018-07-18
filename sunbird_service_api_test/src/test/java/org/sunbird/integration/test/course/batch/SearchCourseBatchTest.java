package org.sunbird.integration.test.course.batch;

import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;

public class SearchCourseBatchTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_SEARCH_COURSE_BATCH_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testSearchCourseBatchFailureWithoutAccessToken";

  public static final String TEMPLATE_DIR = "templates/course/batch/search";

  private String getSearchCourseBatchUrl() {

    return getLmsApiUriPath("/api/course/v1/batch/list", "/v1/course/batch/search");
  }

  @DataProvider(name = "searchFailureUserDataProvider")
  public Object[][] searchFailureUserDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_SEARCH_COURSE_BATCH_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
    };
  }

  @Test(dataProvider = "searchFailureUserDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchCourseBatchFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getSearchCourseBatchUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
