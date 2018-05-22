package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.sunbird.common.util.Constants;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateIssuerTest extends TestNGCitrusTestDesigner {

    public static final String TEMPLATE_DIR = "templates/badge/issuer/create";
    public static final String TEST_SUCCESS_DIR = TEMPLATE_DIR + "/createIssuerSuccess/";

    public static final String REQUEST_FORM_DATA = "request.params";
    public static final String RESPONSE_JSON = "response.json";

    @Autowired
    private HttpClient restTestClient;

    @Autowired
    private TestGlobalProperty initGlobalValues;

    private String getCreateIssuerUrl() {
        return initGlobalValues.getLmsUrl().contains("localhost") ? "/v1/issuer/create" : "/badging/v1/issuer/create";
    }

    private void performTest(String testName, HttpClient httpClient, TestGlobalProperty config,
                             String url, String requestFormData, String responseJson) {
        System.out.println(requestFormData);

        getTestCase().setName(testName);

        new HttpUtil().multipartPost(http().client(httpClient), config, url, requestFormData);

        http().client(httpClient).receive().response(HttpStatus.OK)
                .payload(new ClassPathResource(responseJson));
    }

    @DataProvider(name = "createIssuerDataProvider")
    public Object[][] createIssuerDataProvider() {
        return new Object[][] {
                new Object[] {
                        TEST_SUCCESS_DIR + REQUEST_FORM_DATA, TEST_SUCCESS_DIR + RESPONSE_JSON, "createIssuerSuccess"
                }
        };
    }

    @Test(dataProvider = "createIssuerDataProvider")
    @CitrusParameters({"requestFormData", "responseJson", "testName"})
    @CitrusTest
    public void testCreateIssuer(String requestFormData, String responseJson, String testName) {
        System.out.println("initGlobalValues = " + initGlobalValues);
        performTest(testName, restTestClient, initGlobalValues, getCreateIssuerUrl(), requestFormData, responseJson);
    }

}
