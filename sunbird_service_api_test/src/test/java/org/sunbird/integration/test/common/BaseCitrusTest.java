package org.sunbird.integration.test.common;

import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

/**
 * Class to contain the common things for all citrus tests.
 *
 * @author arvind.
 */
public class BaseCitrusTest extends TestNGCitrusTestDesigner {

  public static final String REQUEST_JSON = "request.json";
  public static final String RESPONSE_JSON = "response.json";

  public static Map<String, List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
  public static Map<String, List<String>> toDeleteCassandraRecordsMap =
      new HashMap<String, List<String>>();
  public static Map<String, List<String>> toDeleteEsRecordsMap =
      new HashMap<String, List<String>>();

  public void performPostTest(
      String testName,
      String testTemplateDir,
      HttpClient httpClient,
      String url,
      String contentType,
      String requestFile,
      String responseFile,
      HttpStatus responseCode,
      Map<String, Object> headers) {
    System.out.println(requestFile);

    getTestCase().setName(testName);

    String requestFilePath =
        MessageFormat.format("{0}/{1}/{2}", testTemplateDir, testName, requestFile);
    String responseFilePath =
        MessageFormat.format("{0}/{1}/{2}", testTemplateDir, testName, responseFile);

    // Send request
    new HttpUtil().post(http().client(httpClient), url, contentType, requestFilePath, headers);

    // Verify response
    http()
        .client(httpClient)
        .receive()
        .response(responseCode)
        .payload(new ClassPathResource(responseFilePath));
  }

  public void performMultipartTest(
      String testName,
      String testTemplateDir,
      HttpClient httpClient,
      TestGlobalProperty config,
      String url,
      String requestFormData,
      String responseJson,
      HttpStatus responseCode,
      Map<String, Object> headers) {
    System.out.println(requestFormData);

    getTestCase().setName(testName);

    String testFolderPath = MessageFormat.format("{0}/{1}", testTemplateDir, testName);

    new HttpUtil()
        .multipartPost(
            http().client(httpClient), config, url, requestFormData, testFolderPath, headers);

    http()
        .client(httpClient)
        .receive()
        .response(responseCode)
        .payload(new ClassPathResource(responseJson));
  }
}
