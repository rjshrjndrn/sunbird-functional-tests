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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.util.Constant;
import org.sunbird.common.util.HTTPMethod;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpClientRequestActionBuilder;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder.HttpClientReceiveActionBuilder;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Class to contain the common things for all citrus  tests.
 * @author arvind.
 */
public class BaseCitrusTest extends TestNGCitrusTestDesigner {

	public static Map<String,List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
	public static Map<String,List<String>> toDeleteCassandraRecordsMap = new HashMap<String, List<String>>();
	public static Map<String,List<String>> toDeleteEsRecordsMap = new HashMap<String, List<String>>();
	private ObjectMapper objectMapper = new ObjectMapper();


	@Autowired
	protected HttpClient restTestClient;
	@Autowired
	protected TestGlobalProperty initGlobalValues;
	protected String admin_token = null;

	/**
	 * 
	 * Key cloak admin token generation is required for some API to get authenticated
	 * 
	 */
	@Test()
	@CitrusTest
	public void getAdminAuthToken() {

		http().client(initGlobalValues.getKeycloakUrl()).send()
		.post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
		.contentType("application/x-www-form-urlencoded")
		.payload("client_id=admin-cli&username=" + initGlobalValues.getKeycloakAdminUser() + "&password="
				+ initGlobalValues.getKeycloakAdminPass() + "&grant_type=password");
		http().client(initGlobalValues.getKeycloakUrl()).receive().response(HttpStatus.OK)
		.validationCallback(new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
			@Override
			public void validate(Map response, Map<String, Object> headers, TestContext context) {
				Assert.assertNotNull(response.get("access_token"));
				admin_token = (String) response.get("access_token");
			}
		});
	}

	protected void performFailureTest(
			String testName,
			HTTPMethod method,
			String url,
			String requestJsonFileName, /* Request json file path */
			boolean isAccessToken,
			HttpStatus status,
			String responseJson) {


		getTestCase().setName(testName);
		HttpClientRequestActionBuilder httpRequest = null;

		switch(method) {
			case POST:
				httpRequest = http()
				.client(restTestClient)
				.send()	
				.post(url);
	
				break;
			case PUT:
				httpRequest = http()
				.client(restTestClient)
				.send()	
				.put(url);
				break;
			case DELETE:
				httpRequest = http()
				.client(restTestClient)
				.send()	
				.delete(url);
				break;
			case GET:
				httpRequest = http()
				.client(restTestClient)
				.send()	
				.get(url);
				break;
		}


		if(isAccessToken) {
			httpRequest.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token);
		}

		httpRequest.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)	
		.payload(new ClassPathResource(requestJsonFileName));


		http()
		.client(restTestClient)
		.receive()
		.response(status)
		.payload(new ClassPathResource(responseJson));

	}


	protected HttpClientReceiveActionBuilder performSuccessTest(
			String testName,
			HTTPMethod method,
			String url,
			String requestJsonFileName, /* Request json file path */
			boolean isAccessToken,
			HttpStatus status,
			String responseJson) {


		getTestCase().setName(testName);
		HttpClientRequestActionBuilder httpRequest = null;

		switch(method) {
			case POST:
				httpRequest = http()
				.client(restTestClient)
				.send()	
				.post(url);
	
				break;
			case PUT:
				httpRequest = http()
				.client(restTestClient)
				.send()	
				.put(url);
				break;
			case DELETE:
				httpRequest = http()
				.client(restTestClient)
				.send()	
				.delete(url);
				break;
			case GET:
				httpRequest = http()
				.client(restTestClient)
				.send()	
				.get(url);
				break;
		}


		if(isAccessToken) {
			httpRequest.header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token);
		}

		httpRequest.header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
		.contentType(Constant.CONTENT_TYPE_APPLICATION_JSON)	
		.payload(new ClassPathResource(requestJsonFileName));


		
		 HttpClientReceiveActionBuilder response = http().client(restTestClient).receive();
	     
		 return response;
	}

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
