package org.sunbird.common.util;

import com.consol.citrus.dsl.builder.HttpClientActionBuilder;
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

/**
 * Helper class for performing HTTP related APIs.
 *
 * @author Manzarul, B Vinaya Kumar
 */
public class HttpUtil {

  /**
   * This method is written for deleting test data from elastic search.
   *
   * @param url String complete url including the id of the element need to be deleted.
   * @return boolean true if deleted else false;
   */
  public static boolean doDeleteOperation(String url) {
    boolean deleteResponse = true;
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpDelete httpDelete = new HttpDelete(url);
      System.out.println("Executing request " + httpDelete.getRequestLine());
      // Create a custom response handler
      ResponseHandler<String> responseHandler =
          response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
              HttpEntity entity = response.getEntity();
              return entity != null ? EntityUtils.toString(entity) : null;
            } else {
              throw new ClientProtocolException("Unexpected response status: " + status);
            }
          };
      String responseBody = httpclient.execute(httpDelete, responseHandler);
      System.out.println(responseBody);
    } catch (Exception e) {
      deleteResponse = false;
      e.printStackTrace();
    }
    return deleteResponse;
  }

  /**
   * Send multipart HTTP post request with form data.
   *
   * @param httpClientActionBuilder HTTP client action builder to use for sending the request.
   * @param config Configuration (e.g. API key) used in sending HTTP request
   * @param url HTTP URL to use in the request
   * @param formDataFile File path containing each form parameter in a new line in format
   *     (key=value)
   * @param formDataFileFolderPath Folder path containing multipart file resource
   */
  public void multipartPost(
      HttpClientActionBuilder httpClientActionBuilder,
      TestGlobalProperty config,
      String url,
      String formDataFile,
      String formDataFileFolderPath) {
    multipartPost(httpClientActionBuilder, config, url, formDataFile, formDataFileFolderPath, null);
  }

  public void multipartPost(
      HttpClientActionBuilder httpClientActionBuilder,
      TestGlobalProperty config,
      String url,
      String formDataFile,
      String formDataFileFolderPath,
      Map<String, Object> headers) {
    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();

    try (Scanner scanner =
        new Scanner(new File(getClass().getClassLoader().getResource(formDataFile).getFile()))) {

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
        httpClientActionBuilder
            .send()
            .post(url)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(Constant.AUTHORIZATION, Constant.BEARER + config.getApiKey());

    if (null != headers) {
      actionBuilder = addHeaders(actionBuilder, headers);
    }
    actionBuilder.payload(formData);
  }

  private HttpClientRequestActionBuilder addHeaders(
      HttpClientRequestActionBuilder actionBuilder, Map<String, Object> headers) {
    if (headers != null) {
      for (Map.Entry<String, Object> entry : headers.entrySet()) {
        actionBuilder = actionBuilder.header(entry.getKey(), entry.getValue());
      }
    }
    return actionBuilder;
  }
}
