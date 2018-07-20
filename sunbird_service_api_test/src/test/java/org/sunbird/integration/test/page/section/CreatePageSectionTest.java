package org.sunbird.integration.test.page.section;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreatePageSectionTest extends BaseCitrusTestRunner {

  public static final String PAGE_TEST_NAME_CREATE_ROOT_ORG_SUCCESS = "testCreateRootOrgSuccess";

  public static final String TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_ACCESS_TOKEN =
      "testCreatePageSectionFailureWithoutAccessToken";
  public static final String TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_NAME =
      "testCreatePageSectionFailureWithoutName";
  public static final String TEST_NAME_CREATE_PAGE_SECTION_FAILURE_WITHOUT_SECTION_DATA_TYPE =
      "testCreatePageSectionFailureWithoutSectionDataType";

  public static final String TEST_NAME_CREATE_PAGE_SECTION_SUCCESS_WITH_NAME_DATA_TYPE =
      "testCreatePageSectionSuccessWithNameDataType";

  public static final String TEMPLATE_DIR = "templates/page/section/create";
  public static final String ORG_CREATE_ORG_TEMPLATE_DIR = "templates/organisation/create";

  private String getCreatePageUrl() {

    return getLmsApiUriPath("/api/data/v1/page/section/create", "/v1/page/section/create");
  }

  @DataProvider(name = "createPageFailureDataProvider")
  public Object[][] createPageFailureDataProvider() {

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

  @Test(dataProvider = "createPageFailureDataProvider")
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreatePageFailure(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, isAuthRequired);
    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreatePageUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }

  @DataProvider(name = "createPageSuccessDataProvider")
  public Object[][] createPageSuccessDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_CREATE_PAGE_SECTION_SUCCESS_WITH_NAME_DATA_TYPE, true, HttpStatus.OK},
    };
  }

  @Test(dataProvider = "createPageSuccessDataProvider", enabled = true)
  @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
  @CitrusTest
  public void testCreatePageSuccess(
      String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
    getAuthToken(this, true);

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getCreatePageUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        isAuthRequired,
        httpStatusCode,
        RESPONSE_JSON);
  }
}
