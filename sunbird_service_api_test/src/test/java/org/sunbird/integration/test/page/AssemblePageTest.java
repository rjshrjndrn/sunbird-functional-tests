package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.PageUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AssemblePageTest extends BaseCitrusTestRunner {

  private static final String PAGE_NAME =
      "FT_Page_Name-" + String.valueOf(System.currentTimeMillis());
  public static final String BT_TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME = "testCreatePageSuccess";

  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITHOUT_SOURCE =
      "testAssemblePageFailureWithoutSource";
  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITHOUT_NAME =
      "testAssemblePageFailureWithoutName";
  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_INVALID_PAGE =
      "testAssemblePageFailureWithInvalidPage";
  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_INVALID_SOURCE =
      "testAssemblePageFailureWithInvalidSource";
  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_VALID_PAGE =
      "testAssemblePageFailureWithValidPage";

  public static final String TEMPLATE_DIR = "templates/page/assemble";
  public static final String PAGE_CREATE_TEMPLATE_DIR = "templates/page/create";

  private String getAssemblePageUrl() {

    return getLmsApiUriPath("/api/data/v1/page/assemble", "/v1/page/assemble");
  }

  @DataProvider(name = "assemblePageFailureDataProvider")
  public Object[][] assemblePageFailureDataProvider() {

    return new Object[][] {
      new Object[] {TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITHOUT_SOURCE, HttpStatus.BAD_REQUEST},
      new Object[] {TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITHOUT_NAME, HttpStatus.BAD_REQUEST},
      new Object[] {TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_INVALID_PAGE, HttpStatus.NOT_FOUND},
      new Object[] {TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_INVALID_SOURCE, HttpStatus.BAD_REQUEST},
      new Object[] {
        TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_VALID_PAGE, HttpStatus.INTERNAL_SERVER_ERROR
      },
    };
  }

  @Test(dataProvider = "assemblePageFailureDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testAssemblePageFailure(String testName, HttpStatus httpStatusCode) {

    if (testName.equalsIgnoreCase(TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_VALID_PAGE)) {
      variable("pageName", PAGE_NAME);
      beforeTestAssemblePage();
    }

    performPostTest(
        this,
        TEMPLATE_DIR,
        testName,
        getAssemblePageUrl(),
        REQUEST_JSON,
        MediaType.APPLICATION_JSON,
        false,
        httpStatusCode,
        RESPONSE_JSON);
  }

  private void beforeTestAssemblePage() {
    getAuthToken(this, true);
    PageUtil.createPage(
        this,
        testContext,
        PAGE_CREATE_TEMPLATE_DIR,
        BT_TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME,
        HttpStatus.OK);
  }
}
