package org.sunbird.integration.test.badge;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.BadgeClassUtil;
import org.sunbird.common.action.IssuerUtil;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.common.action.TestActionUtil;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GetBadgeClassTest extends BaseCitrusTestRunner {
    private static final String BT_TEST_NAME_CREATE_ISSUER_SUCCESS = "testCreateIssuerSuccess";
    private static final String BT_CREATE_ISSUER_TEMPLATE_DIR = "templates/badge/issuer/create";

    private static final String BT_TEST_NAME_CREATE_BADGE_CLASS_SUCCESS_WITH_TYPE_USER =
            "testCreateBadgeClassSuccessWithTypeUser";
    private static final String BT_CREATE_BADGE_CLASS_TEMPLATE_DIR = "templates/badge/class/create";

    private static final String TEST_NAME_GET_BADGE_CLASS_SUCCESS =
            "testGetBadgeClassSuccess";

    private static final String TEST_NAME_GET_BADGE_CLASS_FAILURE_WITH_INVALID_ID =
            "testGetBadgeClassFailureWithInvalidId";
    private static final String TEST_NAME_GET_BADGE_CLASS_FAILURE_WITH_BLANK_ID =
            "testGetBadgeClassFailureWithBlankId";

    private static final String TEMPLATE_DIR = "templates/badge/class/get";

    private String getReadBadgeClassUrl(String pathParam) {
        return getLmsApiUriPath("/api/badging/v1/issuer/badge/read", "/v1/issuer/badge/read", pathParam);
    }

    @DataProvider(name = "getBadgeClassDataProviderSuccess")
    public Object[][] getBadgeClassDataProviderSuccess() {
        return new Object[][] {new Object[] {TEST_NAME_GET_BADGE_CLASS_SUCCESS}};
    }

    @DataProvider(name = "getBadgeClassDataProviderFailure")
    public Object[][] getBadgeClassDataProviderFailure() {
        return new Object[][] {
                new Object[] {TEST_NAME_GET_BADGE_CLASS_FAILURE_WITH_INVALID_ID, "invalid", Constant.RESPONSE_JSON},
                new Object[] {TEST_NAME_GET_BADGE_CLASS_FAILURE_WITH_BLANK_ID, "", null}
        };
    }

    @Test(dataProvider = "getBadgeClassDataProviderSuccess")
    @CitrusParameters({"testName"})
    @CitrusTest
    public void testGetBadgeClassSuccess(String testName) {
        beforeTest();
        performGetTest(
                this,
                TEMPLATE_DIR,
                testName,
                getReadBadgeClassUrl(TestActionUtil.getVariable(testContext, Constant.EXTRACT_VAR_BADGE_ID)),
                false,
                HttpStatus.OK,
                RESPONSE_JSON);
        afterTest();
    }

    @Test(dataProvider = "getBadgeClassDataProviderFailure")
    @CitrusParameters({"testName", "badgeId", "responseJson"})
    @CitrusTest
    public void testGetBadgeClassFailure(String testName, String badgeId, String responseJson) {
        performGetTest(
                this,
                TEMPLATE_DIR,
                testName,
                getReadBadgeClassUrl(badgeId),
                false,
                HttpStatus.NOT_FOUND,
                responseJson);
    }

    private void beforeTest() {
        getAuthToken(this, true);
        variable("rootOrgChannel", OrgUtil.getRootOrgChannel());
        OrgUtil.getRootOrgId(this, testContext);
        IssuerUtil.createIssuer(
                this,
                testContext,
                config,
                BT_CREATE_ISSUER_TEMPLATE_DIR,
                BT_TEST_NAME_CREATE_ISSUER_SUCCESS,
                HttpStatus.OK);
        BadgeClassUtil.createBadgeClass(
                this,
                testContext,
                config,
                BT_CREATE_BADGE_CLASS_TEMPLATE_DIR,
                BT_TEST_NAME_CREATE_BADGE_CLASS_SUCCESS_WITH_TYPE_USER,
                HttpStatus.OK);
    }

    private void afterTest() {}
}
