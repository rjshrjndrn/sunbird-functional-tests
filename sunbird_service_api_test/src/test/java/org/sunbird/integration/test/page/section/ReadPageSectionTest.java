package org.sunbird.integration.test.page.section;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReadPageSectionTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_READ_PAGE_SECTIONS_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testReadPageSectionsFailureWithoutAccessToken";

  public static final String TEST_NAME_READ_PAGE_SECTIONS_FAILURE_WITH_INVALID_ID =
      "testReadPageSectionsFailureWithInvalidId";

  public static final String TEMPLATE_DIR = "templates/page/read";

  private String getReadPageSectionUrl() {
    return getLmsApiUriPath("/api/data/v1/page/section/read", "/v1/page/section/read");
  }

  @DataProvider(name = "readPageSectionFailureDataProvider")
  public Object[][] readPageSectionFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_READ_PAGE_SECTIONS_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED,
      },
      new Object[] {
        TEST_NAME_READ_PAGE_SECTIONS_FAILURE_WITH_INVALID_ID, true, HttpStatus.NOT_FOUND,
      },
    };
  }

  @Test(dataProvider = "readPageSectionFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testReadPageSectionFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);

    String url = getReadPageSectionUrl() + "/invalid";

    performGetTest(
        this, TEMPLATE_DIR, testName, url, isAuthRequired, httpStatusCode, RESPONSE_JSON);
  }
}
