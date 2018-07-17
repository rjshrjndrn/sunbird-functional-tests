package org.sunbird.common.action;

import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpActionBuilder;
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  public static TestAction getTokenRequestTestAction(
      HttpActionBuilder builder, String endpointName) {
    String userName = System.getenv("sunbird_username");
    String password = System.getenv("sunbird_user_password");
    return getTokenRequestTestAction(builder, endpointName, userName, password);
  }

  public static TestAction getTokenRequestTestAction(
      HttpActionBuilder builder, String endpointName, String userName, String password) {
    String urlPath =
        "/realms/" + System.getenv("sunbird_sso_realm") + "/protocol/openid-connect/token";
    return builder
        .client(endpointName)
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
      HttpActionBuilder builder, String endpointName) {
    return builder
        .client(endpointName)
        .receive()
        .response(HttpStatus.OK)
        .messageType(MessageType.JSON)
        .extractFromPayload("$.access_token", "accessToken");
  }

  public static TestAction getPostRequestTestAction(
      HttpActionBuilder builder,
      TestCase testCase,
      String endpointName,
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
        builder.client(endpointName).send().post(url).messageType(MessageType.JSON);
    if (StringUtils.isNotBlank(contentType)) {
      requestActionBuilder.contentType(contentType);
    }
    addHeaders(requestActionBuilder, headers);
    return requestActionBuilder.payload(new ClassPathResource(requestFilePath));
  }

  public static TestAction getMultipartRequestTestAction(
      TestContext context,
      HttpActionBuilder builder,
      String endpointName,
      String testTemplateDir,
      String testName,
      String requestUrl,
      String requestFile,
      Map<String, Object> headers,
      ClassLoader classLoader,
      TestGlobalProperty config) {
    System.out.println("context = " + context);

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
            formData.add(param[0], TestActionUtil.getVariable(context, param[1]));
          }
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    HttpClientRequestActionBuilder actionBuilder =
        builder
            .client(endpointName)
            .send()
            .post(requestUrl)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(Constant.AUTHORIZATION, Constant.BEARER + config.getApiKey());

    if (null != headers) {
      actionBuilder = addHeaders(actionBuilder, headers);
    }
    return actionBuilder.payload(formData);
  }

  public static TestAction getResponseTestAction(
      HttpActionBuilder builder,
      String endpointName,
      String testTemplateDir,
      String testName,
      HttpStatus responseCode,
      String responseFile) {
    String responseFilePath =
        MessageFormat.format("{0}/{1}/{2}", testTemplateDir, testName, responseFile);

    return builder
        .client(endpointName)
        .receive()
        .response(responseCode)
        .validator("defaultJsonMessageValidator")
        .messageType(MessageType.JSON)
        .payload(new ClassPathResource(responseFilePath));
  }

  public static TestAction getExtractFromResponseTestAction(
      TestContext testContext,
      HttpActionBuilder builder,
      String endpointName,
      HttpStatus responseCode,
      String extractFieldPath,
      String extractVariable) {
    ObjectMapper mapper = new ObjectMapper();
    return builder
        .client(endpointName)
        .receive()
        .response(responseCode)
        .validator("defaultJsonMessageValidator")
        .messageType(MessageType.JSON)
        .extractFromPayload(extractFieldPath, extractVariable)
        .validationCallback(
            new JsonMappingValidationCallback<Map>(Map.class, mapper) {
              @Override
              public void validate(Map response, Map<String, Object> headers, TestContext context) {
                String extractValue =
                    (String) context.getVariables().getOrDefault(extractVariable, extractVariable);
                testContext.getVariables().put(extractVariable, extractValue);
                System.out.println("extractVariable = " + extractValue);
              }
            });
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

  public static String getVariable(TestContext testContext, String variableName) {
    String value;
    try {
      value = testContext.getVariable(variableName);
    } catch (CitrusRuntimeException exception) {
      value = variableName;
    }
    return value;
  }
}
