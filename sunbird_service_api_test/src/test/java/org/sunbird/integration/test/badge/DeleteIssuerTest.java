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

public class DeleteIssuerTest extends BaseCitrusTestRunner {
    private static final String BT_TEST_NAME_CREATE_ISSUER_SUCCESS = "testCreateIssuerSuccess";
    private static final String BT_CREATE_ISSUER_TEMPLATE_DIR = "templates/badge/issuer/create";

    private static final String TEST_NAME_DELETE_ISSUER_SUCCESS =
            "testDeleteIssuerSuccess";

    private static final String TEST_NAME_DELETE_ISSUER_FAILURE_WITH_INVALID_ID =
            "testDeleteIssuerFailureWithInvalidId";
    private static final String TEST_NAME_DELETE_ISSUER_FAILURE_WITH_BLANK_ID =
            "testDeleteIssuerFailureWithBlankId";

    public static final String TEMPLATE_DIR = "templates/badge/issuer/delete";

    private String getDeleteIssuerUrl(String pathParam) {
        return getLmsApiUriPath("/api/badging/v1/issuer/delete", "/v1/issuer/delete", pathParam);
    }

    @DataProvider(name = "getDeleteDataProviderSuccess")
    public Object[][] getDeleteDataProviderSuccess() {
        return new Object[][] {new Object[] {TEST_NAME_DELETE_ISSUER_SUCCESS}};
    }

    @DataProvider(name = "getDeleteDataProviderFailure")
    public Object[][] getDeleteDataProviderFailure() {
        return new Object[][] {
                new Object[] {TEST_NAME_DELETE_ISSUER_FAILURE_WITH_INVALID_ID, "invalid", Constant.RESPONSE_JSON},
                new Object[] {TEST_NAME_DELETE_ISSUER_FAILURE_WITH_BLANK_ID, "", null}
        };
    }

    @Test(dataProvider = "getDeleteDataProviderSuccess")
    @CitrusParameters({"testName"})
    @CitrusTest
    public void testDeleteIssuerSuccess(String testName) {
        beforeTest();
        performDeleteTest(
                this,
                TEMPLATE_DIR,
                testName,
                getDeleteIssuerUrl(TestActionUtil.getVariable(testContext, "issuerId")),
                null,
                null,
                false,
                HttpStatus.OK,
                RESPONSE_JSON);
    }

    @Test(dataProvider = "getDeleteDataProviderFailure")
    @CitrusParameters({"testName", "issuerId", "responseJson"})
    @CitrusTest
    public void testDeleteIssuerFailure(String testName, String issuerId, String responseJson) {
        performDeleteTest(
                this,
                TEMPLATE_DIR,
                testName,
                getDeleteIssuerUrl(issuerId),
                null,
                null,
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
}
