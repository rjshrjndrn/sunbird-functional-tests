package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import java.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateIssuerTest extends BaseCitrusTest {

  public static final String TEST_NAME_CREATE_ISSUER_SUCCESS = "createIssuerSuccess";
  public static final String TEST_NAME_CREATE_ISSUER_SUCCESS_WITH_IMAGE =
      "createIssuerSuccessWithImage";
  public static final String TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_NAME =
      "createIssuerFailureWithoutName";
  public static final String TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_DESCRIPTION =
      "createIssuerFailureWithoutDescription";
  public static final String TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_URL =
      "createIssuerFailureWithoutUrl";
  public static final String TEST_NAME_CREATE_ISSUER_FAILURE_WITH_INVALID_URL =
      "createIssuerFailureWithInvalidUrl";
  public static final String TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_EMAIL =
      "createIssuerFailureWithoutEmail";
  public static final String TEST_NAME_CREATE_ISSUER_FAILURE_WITH_INVALID_EMAIL =
      "createIssuerFailureWithInvalidEmail";

  public static final String TEMPLATE_DIR = "templates/badge/issuer/create";
  public static final String TEST_DIR_CREATE_ISSUER_SUCCESS =
      MessageFormat.format("{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_SUCCESS);
  public static final String TEST_DIR_CREATE_ISSUER_SUCCESS_WITH_IMAGE =
      MessageFormat.format("{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_SUCCESS_WITH_IMAGE);
  public static final String TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_NAME =
      MessageFormat.format("{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_NAME);
  public static final String TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_DESCRIPTION =
      MessageFormat.format(
          "{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_DESCRIPTION);
  public static final String TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_URL =
      MessageFormat.format("{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_URL);
  public static final String TEST_DIR_CREATE_ISSUER_FAILURE_WITH_INVALID_URL =
      MessageFormat.format(
          "{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_FAILURE_WITH_INVALID_URL);
  public static final String TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_EMAIL =
      MessageFormat.format("{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_EMAIL);
  public static final String TEST_DIR_CREATE_ISSUER_FAILURE_WITH_INVALID_EMAIL =
      MessageFormat.format(
          "{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_FAILURE_WITH_INVALID_EMAIL);

  public static final String REQUEST_FORM_DATA = "request.params";
  public static final String RESPONSE_JSON = "response.json";

  @Autowired private HttpClient restTestClient;

  @Autowired private TestGlobalProperty initGlobalValues;

  private String getCreateIssuerUrl() {
    System.out.println("initGlobalValues = " + initGlobalValues);
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/issuer/create"
        : "/api/badging/v1/issuer/create";
  }

  @DataProvider(name = "createIssuerDataProviderSuccess")
  public Object[][] createIssuerDataProviderSuccess() {
    return new Object[][] {
      new Object[] {
        TEST_DIR_CREATE_ISSUER_SUCCESS + REQUEST_FORM_DATA,
        TEST_DIR_CREATE_ISSUER_SUCCESS + RESPONSE_JSON,
        TEST_NAME_CREATE_ISSUER_SUCCESS
      },
      new Object[] {
        TEST_DIR_CREATE_ISSUER_SUCCESS_WITH_IMAGE + REQUEST_FORM_DATA,
        TEST_DIR_CREATE_ISSUER_SUCCESS_WITH_IMAGE + RESPONSE_JSON,
        TEST_NAME_CREATE_ISSUER_SUCCESS_WITH_IMAGE
      }
    };
  }

  @DataProvider(name = "createIssuerDataProviderFailure")
  public Object[][] createIssuerDataProviderFailure() {
    return new Object[][] {
      new Object[] {
        TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_NAME + REQUEST_FORM_DATA,
        TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_NAME + RESPONSE_JSON,
        TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_NAME
      },
      new Object[] {
        TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_DESCRIPTION + REQUEST_FORM_DATA,
        TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_DESCRIPTION + RESPONSE_JSON,
        TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_DESCRIPTION
      },
      new Object[] {
        TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_URL + REQUEST_FORM_DATA,
        TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_URL + RESPONSE_JSON,
        TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_URL
      },
      new Object[] {
        TEST_DIR_CREATE_ISSUER_FAILURE_WITH_INVALID_URL + REQUEST_FORM_DATA,
        TEST_DIR_CREATE_ISSUER_FAILURE_WITH_INVALID_URL + RESPONSE_JSON,
        TEST_NAME_CREATE_ISSUER_FAILURE_WITH_INVALID_URL
      },
      new Object[] {
        TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_EMAIL + REQUEST_FORM_DATA,
        TEST_DIR_CREATE_ISSUER_FAILURE_WITHOUT_EMAIL + RESPONSE_JSON,
        TEST_NAME_CREATE_ISSUER_FAILURE_WITHOUT_EMAIL
      },
      new Object[] {
        TEST_DIR_CREATE_ISSUER_FAILURE_WITH_INVALID_EMAIL + REQUEST_FORM_DATA,
        TEST_DIR_CREATE_ISSUER_FAILURE_WITH_INVALID_EMAIL + RESPONSE_JSON,
        TEST_NAME_CREATE_ISSUER_FAILURE_WITH_INVALID_EMAIL
      }
    };
  }

  @Test(dataProvider = "createIssuerDataProviderSuccess")
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  public void testCreateIssuerSuccess(
      String requestFormData, String responseJson, String testName) {
    System.out.println("initGlobalValues = " + initGlobalValues);
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        restTestClient,
        initGlobalValues,
        getCreateIssuerUrl(),
        requestFormData,
        responseJson,
        HttpStatus.OK);
  }

  @Test(dataProvider = "createIssuerDataProviderFailure")
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  public void testCreateIssuerFailure(
      String requestFormData, String responseJson, String testName) {
    System.out.println("initGlobalValues = " + initGlobalValues);
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        restTestClient,
        initGlobalValues,
        getCreateIssuerUrl(),
        requestFormData,
        responseJson,
        HttpStatus.BAD_REQUEST);
  }
}
