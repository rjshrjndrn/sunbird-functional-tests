package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateBadgeClassTest extends BaseCitrusTest {
  public static final String TEST_NAME_CREATE_BADGE_CLASS_SUCCESS = "testCreateBadgeClassSuccess";

  public static final String TEMPLATE_DIR = "templates/badge/class/create";

  private String getCreateBadgeClassUrl() {
    return getLmsApiUriPath("/api/badging/v1/issuer/badge/create", "/v1/issuer/badge/create");
  }

  @DataProvider(name = "createBadgeClassDataProviderSuccess")
  public Object[][] createBadgeClassDataProviderSuccess() {
    return new Object[][] {
      new Object[] {REQUEST_FORM_DATA, RESPONSE_JSON, TEST_NAME_CREATE_BADGE_CLASS_SUCCESS}
    };
  }

  @Test(dataProvider = "createBadgeClassDataProviderSuccess")
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  public void testCreateBadgeClassSuccess(
      String requestFormData, String responseJson, String testName) {
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getCreateBadgeClassUrl(),
        requestFormData,
        HttpStatus.OK,
        responseJson,
        false);
  }
}
