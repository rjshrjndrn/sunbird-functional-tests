package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.IssuerUtil;
import org.sunbird.common.action.TestActionUtil;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GetIssuerTest extends BaseCitrusTestRunner {
    private static final String BT_TEST_NAME_CREATE_ISSUER_SUCCESS = "testCreateIssuerSuccess";
    private static final String BT_CREATE_ISSUER_TEMPLATE_DIR = "templates/badge/issuer/create";

    private static final String TEST_NAME_GET_ISSUER_SUCCESS =
            "testGetIssuerSuccess";

    private static final String TEST_NAME_GET_ISSUER_FAILURE_WITH_INVALID_ID =
            "testGetIssuerFailureWithInvalidId";
    private static final String TEST_NAME_GET_ISSUER_FAILURE_WITH_BLANK_ID =
            "testGetIssuerFailureWithBlankId";

    public static final String TEMPLATE_DIR = "templates/badge/issuer/get";

    private String getReadIssuerUrl(String pathParam) {
        return getLmsApiUriPath("/api/badging/v1/issuer/read", "/v1/issuer/read", pathParam);
    }

    @DataProvider(name = "getIssuerDataProviderSuccess")
    public Object[][] getIssuerDataProviderSuccess() {
        return new Object[][] {new Object[] {TEST_NAME_GET_ISSUER_SUCCESS}};
    }

    @DataProvider(name = "getIssuerDataProviderFailure")
    public Object[][] getIssuerDataProviderFailure() {
        return new Object[][] {
                new Object[] {TEST_NAME_GET_ISSUER_FAILURE_WITH_INVALID_ID, "invalid", Constant.RESPONSE_JSON},
                new Object[] {TEST_NAME_GET_ISSUER_FAILURE_WITH_BLANK_ID, "", null}
        };
    }

    @Test(dataProvider = "getIssuerDataProviderSuccess")
    @CitrusParameters({"testName"})
    @CitrusTest
    public void getIssuerDataProviderSuccess(String testName) {
        beforeTest();
        performGetTest(
                this,
                TEMPLATE_DIR,
                testName,
                getReadIssuerUrl(TestActionUtil.getVariable(testContext, "issuerId")),
                false,
                HttpStatus.OK,
                RESPONSE_JSON);
        afterTest();
    }

    @Test(dataProvider = "getIssuerDataProviderFailure")
    @CitrusParameters({"testName", "issuerId", "responseJson"})
    @CitrusTest
    public void getIssuerDataProviderFailure(String testName, String issuerId, String responseJson) {
        performGetTest(
                this,
                TEMPLATE_DIR,
                testName,
                getReadIssuerUrl(issuerId),
                false,
                HttpStatus.NOT_FOUND,
                responseJson);
    }

    private void beforeTest() {
        IssuerUtil.createIssuer(
                this,
                testContext,
                config,
                BT_CREATE_ISSUER_TEMPLATE_DIR,
                BT_TEST_NAME_CREATE_ISSUER_SUCCESS,
                HttpStatus.OK);
    }

    private void afterTest() {}
}
