package org.sunbird.integration.test.common;

import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpActionBuilder;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder;
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
import com.consol.citrus.message.MessageType;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

public class TestActionUtil {
  public static TestAction getTokenRequestTestAction(HttpClientActionBuilder builder) {
    String userName ="rajat";// System.getenv("sunbird_username");
    String password = "rajat";//System.getenv("sunbird_user_password");
    return getTokenRequestTestAction(builder, userName, password);
  }

  public static TestAction getTokenRequestTestAction(
      HttpClientActionBuilder builder, String userName, String password) {
    String urlPath =
        "/realms/" + System.getenv("sunbird_sso_realm") + "/protocol/openid-connect/token";
    return builder
        .send()
        .post(urlPath)
        .contentType("application/x-www-form-urlencoded")
        .payload(
            "client_id="
                + System.getenv("sunbird_sso_client_id")
                + "&username="
                + userName
                + "&password="
                + password
                + "&grant_type=password");
  }

  public static TestAction getTokenResponseTestAction(
      HttpClientActionBuilder builder, TestCase testCase) {
    return builder
        .receive()
        .response(HttpStatus.OK)
        .messageType(MessageType.JSON)
        .extractFromPayload("$.access_token", "accessToken");
  }

  public static TestAction getPostRequestTestAction(
      HttpClientActionBuilder builder,
      TestCase testCase,
      String testName,
      String testTemplateDir,
      String url,
      String contentType,
      String requestFile,
      Map<String, Object> headers) {

    testCase.setName(testName);

    String requestFilePath =
        MessageFormat.format("{0}/{1}/{2}", testTemplateDir, testName, requestFile);
    HttpClientRequestActionBuilder requestActionBuilder =
        builder.send().post(url).messageType(MessageType.JSON);
    if (StringUtils.isNotBlank(contentType)) {
      requestActionBuilder.contentType(contentType);
    }
    addHeaders(requestActionBuilder, headers);

    return requestActionBuilder.payload(new ClassPathResource(requestFilePath));
  }

  public static TestAction getMultipartRequestTestAction(
      HttpClientActionBuilder builder,
      TestCase testCase,
      String testName,
      String testTemplateDir,
      String url,
      String requestFile,
      Map<String, Object> headers,
      ClassLoader classLoader,
      TestGlobalProperty config) {

    testCase.setName(testName);

    String formDataFileFolderPath = MessageFormat.format("{0}/{1}", testTemplateDir, testName);
    String formDataFile =
        MessageFormat.format("{0}/{1}/{2}", testTemplateDir, testName, requestFile);

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

    try (Scanner scanner = new Scanner(new File(classLoader.getResource(formDataFile).getFile()))) {

      while (scanner.hasNext()) {
        String[] param = scanner.nextLine().split(Constant.EQUAL_SIGN);
        if (param != null && param.length == 2) {
          if (param[0].equalsIgnoreCase(Constant.MULTIPART_FILE_NAME)) {
            formData.add(
                Constant.MULTIPART_FILE_NAME,
                new ClassPathResource(formDataFileFolderPath + "/" + param[1]));
          } else {
            formData.add(param[0], param[1]);
          }
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    HttpClientRequestActionBuilder actionBuilder =
        builder
            .send()
            .post(url)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(Constant.AUTHORIZATION, Constant.BEARER + config.getApiKey());

    if (null != headers) {
      actionBuilder = addHeaders(actionBuilder, headers);
    }
    return actionBuilder.payload(formData);
  }

  public static TestAction getResponseTestAction(
      HttpClientActionBuilder builder,
      String testName,
      String testTemplateDir,
      HttpStatus responseCode,
      String responseFile) {
    String responseFilePath =
        MessageFormat.format("{0}/{1}/{2}", testTemplateDir, testName, responseFile);

    return builder
        .receive()
        .response(responseCode)
        .validator("defaultJsonMessageValidator")
        .messageType(MessageType.JSON)
        .payload(new ClassPathResource(responseFilePath));
  }

  public static Map<String, Object> getHeaders(boolean isAuthRequired) {
    Map<String, Object> headers = new HashMap<>();
    if (isAuthRequired) {
      headers.put(Constant.X_AUTHENTICATED_USER_TOKEN, "${accessToken}");
    }
    headers.put(Constant.AUTHORIZATION, Constant.BEARER + System.getenv("sunbird_api_key"));
    return headers;
  }

  private static HttpClientRequestActionBuilder addHeaders(
      HttpClientRequestActionBuilder actionBuilder, Map<String, Object> headers) {
    if (headers != null) {
      for (Map.Entry<String, Object> entry : headers.entrySet()) {
        actionBuilder = actionBuilder.header(entry.getKey(), entry.getValue());
      }
    }
    return actionBuilder;
  }

}
