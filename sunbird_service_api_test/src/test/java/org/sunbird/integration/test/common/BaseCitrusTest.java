package org.sunbird.integration.test.common;

import com.consol.citrus.dsl.builder.HttpClientActionBuilder;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

/**
 * Class to contain the common things for all citrus tests.
 *
 * @author arvind.
 */
public class BaseCitrusTest extends TestNGCitrusTestDesigner {

  public static Map<String, List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
  public static Map<String, List<String>> toDeleteCassandraRecordsMap =
      new HashMap<String, List<String>>();
  public static Map<String, List<String>> toDeleteEsRecordsMap =
      new HashMap<String, List<String>>();
  public static String userAuthToken;
  public static final String REQUEST_JSON = "request.json";
  public static final String RESPONSE_JSON = "response.json";

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

  /**
   * This method will provide default user auth token.This token will be generated only once.
   *
   * @return user auth token
   */
  public static String getUserToken() {
    return getUserAuthToken(null, null, false);
  }

  /**
   * This method will generate the auth token of provided user details. if username or password is
   * null or empty then it will return null. This will generate every time a new token.
   *
   * @param userName username inside system
   * @param password password inside system
   * @return authToken
   */
  public String getUserToken(String userName, String password) {
    if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
      System.out.println(
          "BaseCitrusTest:getUserToken username and password is either null or empty.");
      return null;
    }
    return getUserAuthToken(userName, password, true);
  }

  private static String getUserAuthToken(
      String userName, String password, boolean isNewTokenRequired) {
    if (StringUtils.isBlank(userName)) {
      userName = System.getenv("sunbird_username");
      password = System.getenv("sunbird_user_password");
    }
    if (isNewTokenRequired) {
      return getToken(userName, password);
    } else {
      if (StringUtils.isBlank(userAuthToken)) {
        userAuthToken = getToken(userName, password);
      }
      return userAuthToken;
    }
  }

  private static String getToken(String userName, String password) {
    String url =
        System.getenv("sunbird_test_base_url")
            + "/auth/realms/"
            + System.getenv("sunbird_sso_realm")
            + "/protocol/openid-connect/token";
    return HttpUtil.getUserAuthToken(
        url, userName, password, System.getenv("sunbird_sso_client_id"));
  }

  public String getTemplateFilePath(String testTemplateDir, String testName, String fileName) {
    return MessageFormat.format("{0}/{1}/", testTemplateDir, testName, fileName);
  }

  public Map<String, Object> getHeaderWithAuthToken(
      HttpClientActionBuilder httpClientActionBuilder) {
    Map<String, Object> header = new HashMap<>();
    header.put(Constant.X_AUTHENTICATED_USER_TOKEN, getUserToken());
    return header;
  }
}
