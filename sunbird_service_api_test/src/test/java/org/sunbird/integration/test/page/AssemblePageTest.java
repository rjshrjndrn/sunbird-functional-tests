package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AssemblePageTest extends BaseCitrusTestRunner {

  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITHOUT_SOURCE =
      "testAssemblePageFailureWithoutSource";
  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITHOUT_NAME =
      "testAssemblePageFailureWithoutName";
  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_INVALID_PAGE =
      "testAssemblePageFailureWithInvalidPage";
  public static final String TEST_NAME_ASSEMBLE_PAGE_FAILURE_WITH_INVALID_SOURCE =
      "testAssemblePageFailureWithInvalidSource";

  public static final String TEMPLATE_DIR = "templates/page/assemble";

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
    };
  }

  @Test(dataProvider = "assemblePageFailureDataProvider")
  @CitrusParameters({"testName", "httpStatusCode"})
  @CitrusTest
  public void testAssemblePageFailure(String testName, HttpStatus httpStatusCode) {

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
}
