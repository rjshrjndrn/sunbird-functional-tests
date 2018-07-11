package org.sunbird.integration.test.common;

import com.consol.citrus.TestAction;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

public class BaseCitrusTest extends TestNGCitrusTestDesigner {

  @Autowired private TestGlobalProperty config;

  public static final String REQUEST_FORM_DATA = "request.params";
  public static final String REQUEST_JSON = "request.json";
  public static final String RESPONSE_JSON = "response.json";

  public static Map<String, List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
  public static Map<String, List<String>> toDeleteCassandraRecordsMap =
      new HashMap<String, List<String>>();
  public static Map<String, List<String>> toDeleteEsRecordsMap =
      new HashMap<String, List<String>>();

  public void performPostTest(
      String testName,
      String templateDir,
      String requestUrl,
      String requestJson,
      HttpStatus responseCode,
      String responseJson,
      boolean isAuthRequired) {

    List<TestAction> actionList = new ArrayList<>();
    addAuthActions(actionList, isAuthRequired);

    actionList.add(
        TestActionUtil.getPostRequestTestAction(
            http().client("restTestClient"),
            getTestCase(),
            testName,
            templateDir,
            requestUrl,
            MediaType.APPLICATION_JSON.toString(),
            requestJson,
            TestActionUtil.getHeaders(isAuthRequired)));
    actionList.add(
        TestActionUtil.getResponseTestAction(
            http().client("restTestClient"), testName, templateDir, responseCode, responseJson));
    sequential().actions(actionList.toArray(new TestAction[0]));
  }

  public void performMultipartTest(
      String testName,
      String templateDir,
      String requestUrl,
      String requestJson,
      HttpStatus responseCode,
      String responseJson,
      boolean isAuthRequired) {
    List<TestAction> actionList = new ArrayList<>();
    addAuthActions(actionList, isAuthRequired);

    actionList.add(
        TestActionUtil.getMultipartRequestTestAction(
            http().client("restTestClient"),
            getTestCase(),
            testName,
            templateDir,
            requestUrl,
            requestJson,
            TestActionUtil.getHeaders(isAuthRequired),
            getClass().getClassLoader(),
            config));
    actionList.add(
        TestActionUtil.getResponseTestAction(
            http().client("restTestClient"), testName, templateDir, responseCode, responseJson));
    sequential().actions(actionList.toArray(new TestAction[0]));
  }

  public void performMultipartTest(
      String testName,
      String testTemplateDir,
      HttpClient httpClient,
      TestGlobalProperty config,
      String url,
      String requestFile,
      String responseFile,
      HttpStatus responseCode,
      Map<String, Object> headers) {
    System.out.println(requestFile);

    getTestCase().setName(testName);

    String testFolderPath = MessageFormat.format("{0}/{1}", testTemplateDir, testName);

    String requestFilePath =
        MessageFormat.format("{0}/{1}/{2}", testTemplateDir, testName, requestFile);
    String responseFilePath =
        MessageFormat.format("{0}/{1}/{2}", testTemplateDir, testName, responseFile);

    new HttpUtil()
        .multipartPost(
            http().client(httpClient), config, url, requestFilePath, testFolderPath, headers);

    http()
        .client(httpClient)
        .receive()
        .response(responseCode)
        .payload(new ClassPathResource(responseFilePath));
  }

  public String getLmsApiUriPath(String apiGatewayUriPath, String localUriPath) {
    return config.getLmsUrl().contains("localhost") ? localUriPath : apiGatewayUriPath;
  }

  private void addAuthActions(List<TestAction> actionList, Boolean isAuthRequired) {
    if (isAuthRequired) {
      actionList.add(TestActionUtil.getTokenRequestTestAction(http().client("keycloakTestClient")));
      actionList.add(
          TestActionUtil.getTokenResponseTestAction(
              http().client("keycloakTestClient"), getTestCase()));
    }
  }
}
