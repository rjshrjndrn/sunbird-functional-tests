package org.sunbird.integration.test.page.section;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ListPageSectionTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_LIST_ALL_PAGE_SECTION_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testListAllPageSectionFailureWithoutAccessToken";

  public static final String TEST_NAME_LIST_ALL_PAGE_SECTION_SUCCESS =
      "testListAllPageSectionSuccess";

  public static final String TEMPLATE_DIR = "templates/page/read";

  private String getListPageSectionUrl() {
    return getLmsApiUriPath("/api/data/v1/page/section/list", "/v1/page/section/list");
  }

  @DataProvider(name = "listPageSectionFailureDataProvider")
  public Object[][] listPageSectionFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_LIST_ALL_PAGE_SECTION_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {
        TEST_NAME_LIST_ALL_PAGE_SECTION_SUCCESS, true, HttpStatus.OK,
      },
    };
  }

  @Test(dataProvider = "listPageSectionFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testListPageSectionFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);

    performGetTest(
        this,
        TEMPLATE_DIR,
        testName,
        getListPageSectionUrl(),
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
