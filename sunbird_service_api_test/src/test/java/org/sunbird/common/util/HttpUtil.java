package org.sunbird.common.util;

import com.consol.citrus.dsl.builder.HttpClientActionBuilder;
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
import com.consol.citrus.dsl.builder.SendMessageBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.sunbird.integration.test.user.EndpointConfig;
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
   * @param httpClient HTTP client to use for sending the request.
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

    httpClientActionBuilder
        .send()
        .post(url)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .header(Constant.AUTHORIZATION, Constant.BEARER + config.getApiKey())
        .header(Constant.X_AUTHENTICATED_USER_TOKEN, EndpointConfig.admin_token)
        .payload(formData);
  }

  public void multipartPost(
      HttpClientActionBuilder httpClientActionBuilder,
      TestGlobalProperty config,
      String url,
      String formDataFile,
      String formDataFileFolderPath, Map<String, Object> headers) {
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

    HttpClientRequestActionBuilder actionBuilder = httpClientActionBuilder
        .send()
        .post(url)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .header(Constant.AUTHORIZATION, Constant.BEARER + config.getApiKey());

    actionBuilder = addHeaders(actionBuilder , headers);
    actionBuilder.payload(formData);
    /*httpClientActionBuilder
        .send()
        .post(url)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .header(Constant.AUTHORIZATION, Constant.BEARER + config.getApiKey())
        .header(Constant.X_AUTHENTICATED_USER_TOKEN, EndpointConfig.admin_token)
        .payload(formData);*/
  }

  private HttpClientRequestActionBuilder addHeaders(HttpClientRequestActionBuilder actionBuilder, Map<String, Object> headers) {
    for(Map.Entry<String, Object> entry : headers.entrySet()){
      actionBuilder = actionBuilder.header(entry.getKey(), entry.getValue());
    }
    return actionBuilder;
  }

  /**
   * Makes an HTTP request using POST method to the specified URL.
   *
   * @param requestURL the URL of the remote server
   * @param params A map containing POST data in form of key-value pairs
   * @return String
   * @throws IOException thrown if any I/O error occurred
   */
  public static String sendPostRequest(
      String requestURL, Map<String, String> params, Map<String, String> headers)
      throws IOException {
    HttpURLConnection httpURLConnection = postRequest(requestURL, params, headers);
    String str = getResponse(httpURLConnection);
    return str;
  }

  private static HttpURLConnection postRequest(
      String requestURL, Map<String, String> params, Map<String, String> headers)
      throws IOException {
    HttpURLConnection httpURLConnection = null;
    OutputStreamWriter writer = null;
    try {
      URL url = new URL(requestURL);
      httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setDoInput(true);
      httpURLConnection.setRequestMethod("POST");
      StringBuilder requestParams = new StringBuilder();
      if (params != null && params.size() > 0) {
        httpURLConnection.setDoOutput(true);
        // creates the params string, encode them using URLEncoder
        for (Map.Entry<String, String> entry : params.entrySet()) {
          String key = entry.getKey();
          String value = entry.getValue();
          requestParams.append(URLEncoder.encode(key, "UTF-8"));
          requestParams.append("=").append(URLEncoder.encode(value, "UTF-8"));
          requestParams.append("&");
        }
      }
      if (headers != null && headers.size() > 0) {
        setHeaders(httpURLConnection, headers);
      }
      if (requestParams.length() > 0) {
        writer =
            new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);
        writer.write(requestParams.toString());
        writer.flush();
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (null != writer) {
        try {
          writer.close();
        } catch (IOException e) {

        }
      }
    }
    return httpURLConnection;
  }

  private static void setHeaders(HttpURLConnection httpURLConnection, Map<String, String> headers) {
    Iterator<Entry<String, String>> itr = headers.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, String> entry = itr.next();
      httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
    }
  }

  private static String getResponse(HttpURLConnection httpURLConnection) throws IOException {
    InputStream inStream = null;
    BufferedReader reader = null;
    StringBuilder builder = new StringBuilder();
    try {
      inStream = httpURLConnection.getInputStream();
      reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
      String line = null;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
    } catch (IOException e) {
      throw e;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
        }
      }
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
        }
      }
      if (httpURLConnection != null) {
        httpURLConnection.disconnect();
      }
    }
    return builder.toString();
  }
}
