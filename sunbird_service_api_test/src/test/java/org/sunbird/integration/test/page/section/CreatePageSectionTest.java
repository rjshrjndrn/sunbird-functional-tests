package org.sunbird.integration.test.page.section;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreatePageSectionTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testCreatePageSectionFailureWithoutAccessToken";
  public static final String TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_NAME =
      "testCreatePageSectionFailureWithoutName";
  public static final String TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_SECTION_DATA_TYPE =
      "testCreatePageSectionFailureWithoutSectionDataType";

  public static final String TEST_NAME_CREATE_PAGE_SECTION_SUCCESS_WITH_NAME_DATA_TYPE =
      "testCreatePageSectionSuccessWithNameDataType";

  public static final String TEMPLATE_DIR = "templates/page/section/create";

  private String getCreatePageSectionUrl() {

    return getLmsApiUriPath("/api/data/v1/page/section/create", "/v1/page/section/create");
  }

  @DataProvider(name = "createPageSectionFailureDataProvider")
  public Object[][] createPageSectionFailureDataProvider() {

    return new Object[][] {
      new Object[] {
        TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
      },
      new Object[] {
        TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_NAME, true, HttpStatus.BAD_REQUEST
      },
      new Object[] {
        TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_SECTION_DATA_TYPE,
        true,
        HttpStatus.BAD_REQUEST
      },
    };
  }

  @Test(dataProvider = "createPageSectionFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreatePageSectionFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreatePageSectionUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @DataProvider(name = "createPageSectionSuccessDataProvider")
  public Object[][] createPageSectionSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_CREATE_PAGE_SECTION_SUCCESS_WITH_NAME_DATA_TYPE, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "createPageSectionSuccessDataProvider", enabled = true)
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreatePageSectionSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, true);

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreatePageSectionUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
