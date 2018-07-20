package org.sunbird.integration.test.page.section;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ListPageSectionTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_LIST_ALL_PAGE_SECTIONS_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testListAllPageSectionsFailureWithoutAccessToken";

  public static final String TEST_NAME_LIST_ALL_PAGE_SECTIONS_SUCCESS =
      "testListAllPageSectionsSuccess";

  public static final String TEMPLATE_DIR = "templates/page/read";

  private String getReadPageSectionUrl() {
    return getLmsApiUriPath("/api/data/v1/page/section/list", "/v1/page/section/list");
  }

  @DataProvider(name = "readPageSectionFailureDataProvider")
  public Object[][] readPageSectionFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_LIST_ALL_PAGE_SECTIONS_FAILURE_WITHOUT_ACCESS_TOKEN,
        false,
        HttpStatus.UNAUTHORIZED,
      },
      new Object[] {
        TEST_NAME_LIST_ALL_PAGE_SECTIONS_SUCCESS, true, HttpStatus.OK,
      },
    };
  }

  @Test(dataProvider = "readPageSectionFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testReadPageSectionFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);

    performGetTest(
        this,
        TEMPLATE_DIR,
        testName,
        getReadPageSectionUrl(),
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
