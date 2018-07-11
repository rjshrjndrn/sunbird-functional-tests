package org.sunbird.integration.test.common;

import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder;
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
import com.consol.citrus.message.MessageType;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;

public class TestActionUtil {
  public static TestAction getTokenRequestTestAction(HttpClientActionBuilder builder) {
    String userName = System.getenv("sunbird_username");
    String password = System.getenv("sunbird_user_password");
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

    contentType =
        StringUtils.isNotBlank(contentType) ? contentType : MediaType.APPLICATION_JSON.toString();

    HttpClientRequestActionBuilder requestActionBuilder =
        builder.send().post(url).messageType(MessageType.JSON).contentType(contentType);

    addHeaders(requestActionBuilder, headers);

    return requestActionBuilder.payload(new ClassPathResource(requestFilePath));
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

  public static Map<String, Object> getHeaders() {
    Map<String, Object> headers = new HashMap<>();
    headers.put(Constant.X_AUTHENTICATED_USER_TOKEN, "${accessToken}");
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
