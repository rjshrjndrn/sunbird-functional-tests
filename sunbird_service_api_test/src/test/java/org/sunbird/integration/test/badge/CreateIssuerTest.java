package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateIssuerTest extends TestNGCitrusTestDesigner {

  public static final String TEST_NAME_CREATE_ISSUER_SUCCESS = "createIssuerSuccess";
  public static final String TEST_NAME_CREATE_ISSUER_WITH_IMAGE_SUCCESS = "createIssuerWithImageSuccess";

  public static final String TEMPLATE_DIR = "templates/badge/issuer/create";
  public static final String TEST_DIR_CREATE_ISSUER_SUCCESS = MessageFormat.format("{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_SUCCESS);
  public static final String TEST_DIR_CREATE_ISSUER_WITH_IMAGE_SUCCESS = MessageFormat.format("{0}/{1}/", TEMPLATE_DIR, TEST_NAME_CREATE_ISSUER_WITH_IMAGE_SUCCESS);

  public static final String REQUEST_FORM_DATA = "request.params";
  public static final String RESPONSE_JSON = "response.json";

  @Autowired private HttpClient restTestClient;

  @Autowired private TestGlobalProperty initGlobalValues;

  private String getCreateIssuerUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/issuer/create"
        : "/api/badging/v1/issuer/create";
  }

  private void performTest(
      String testName,
      HttpClient httpClient,
      TestGlobalProperty config,
      String url,
      String requestFormData,
      String responseJson) {
    System.out.println(requestFormData);

    getTestCase().setName(testName);

    String testFolderPath = MessageFormat.format("{0}/{1}", TEMPLATE_DIR, testName);

    new HttpUtil().multipartPost(http().client(httpClient), config, url, requestFormData, testFolderPath);

    http()
        .client(httpClient)
        .receive()
        .response(HttpStatus.OK)
        .payload(new ClassPathResource(responseJson));
  }

  @DataProvider(name = "createIssuerDataProvider")
  public Object[][] createIssuerDataProvider() {
    return new Object[][] {
            new Object[]{
                    TEST_DIR_CREATE_ISSUER_SUCCESS + REQUEST_FORM_DATA,
                    TEST_DIR_CREATE_ISSUER_SUCCESS + RESPONSE_JSON,
                    TEST_NAME_CREATE_ISSUER_SUCCESS
            },
            new Object[]{
                    TEST_DIR_CREATE_ISSUER_WITH_IMAGE_SUCCESS + REQUEST_FORM_DATA,
                    TEST_DIR_CREATE_ISSUER_WITH_IMAGE_SUCCESS + RESPONSE_JSON,
                    TEST_NAME_CREATE_ISSUER_WITH_IMAGE_SUCCESS
            }
    };
  }

  @Test(dataProvider = "createIssuerDataProvider")
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  public void testCreateIssuer(String requestFormData, String responseJson, String testName) {
    System.out.println("initGlobalValues = " + initGlobalValues);
    performTest(
        testName,
        restTestClient,
        initGlobalValues,
        getCreateIssuerUrl(),
        requestFormData,
        responseJson);
  }
}
