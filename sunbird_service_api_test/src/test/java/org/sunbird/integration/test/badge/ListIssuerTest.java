package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.IssuerUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ListIssuerTest extends BaseCitrusTestRunner {
    private static final String BT_TEST_NAME_CREATE_ISSUER_SUCCESS = "testCreateIssuerSuccess";
    private static final String BT_CREATE_ISSUER_TEMPLATE_DIR = "templates/badge/issuer/create";

    private static final String TEST_NAME_LIST_ISSUER_SUCCESS =
            "testListIssuerSuccess";

    public static final String TEMPLATE_DIR = "templates/badge/issuer/list";

    private String getListIssuerUrl() {
        return getLmsApiUriPath("/api/badging/v1/issuer/list", "/v1/issuer/list");
    }

    @DataProvider(name = "listIssuerDataProviderSuccess")
    public Object[][] listIssuerDataProviderSuccess() {
        return new Object[][] {new Object[] {TEST_NAME_LIST_ISSUER_SUCCESS}};
    }

    @Test(dataProvider = "listIssuerDataProviderSuccess")
    @CitrusParameters({"testName"})
    @CitrusTest
    public void testListIssuerSuccess(String testName) {
        beforeTest();
        performGetTest(
                this,
                TEMPLATE_DIR,
                testName,
                getListIssuerUrl(),
                false,
                HttpStatus.OK,
                RESPONSE_JSON);
        afterTest();
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