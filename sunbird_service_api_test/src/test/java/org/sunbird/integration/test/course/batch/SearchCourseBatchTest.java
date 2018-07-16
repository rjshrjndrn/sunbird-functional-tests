package org.sunbird.integration.test.courseBatch;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchCourseBatchTest extends BaseCitrusTest {

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

  /*@Test(dataProvider = "searchFailureUserDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest*/
  public void testSearchCourseBatchFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getSearchCourseBatchUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
