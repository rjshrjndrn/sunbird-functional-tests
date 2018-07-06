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
 * Class to contain the common things for all citrus  tests.
 * @author arvind.
 */
public class BaseCitrusTest extends TestNGCitrusTestDesigner {

  public static Map<String,List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
  public static Map<String,List<String>> toDeleteCassandraRecordsMap = new HashMap<String, List<String>>();
  public static Map<String,List<String>> toDeleteEsRecordsMap = new HashMap<String, List<String>>();

  public void performMultipartTest(
          String testName,
          String testTemplateDir,
          HttpClient httpClient,
          TestGlobalProperty config,
          String url,
          String requestFormData,
          String responseJson,
          HttpStatus responseCode) {
    System.out.println(requestFormData);

    getTestCase().setName(testName);

    String testFolderPath = MessageFormat.format("{0}/{1}", testTemplateDir, testName);

    new HttpUtil().multipartPost(http().client(httpClient), config, url, requestFormData, testFolderPath);

    http()
            .client(httpClient)
            .receive()
            .response(responseCode)
            .payload(new ClassPathResource(responseJson));
  }


}
