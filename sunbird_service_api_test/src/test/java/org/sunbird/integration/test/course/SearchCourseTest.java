package org.sunbird.integration.test.course;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchCourseTest extends BaseCitrusTest {

  public static final String TEST_NAME_SEARCH_COURSE_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testSearchCourseFailureWithoutAccessToken";

  public static final String TEMPLATE_DIR = "templates/course/search";

  private String getSearchCourseUrl() {

    return getLmsApiUriPath("/api/course/v1/search", "/v1/course/search");
  }

  @DataProvider(name = "searchCourseFailureDataProvider")
  public Object[][] searchCourseFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_SEARCH_COURSE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
    };
  }

  @Test(dataProvider = "searchCourseFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testSearchCourseFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

    performPostTest(
        testName,
        TEMPLATE_DIR,
        getSearchCourseUrl(),
        REQUEST_JSON,
        httpStatusCode,
        RESPONSE_JSON,
        isAuthRequired,
        MediaType.APPLICATION_JSON);
  }
}
