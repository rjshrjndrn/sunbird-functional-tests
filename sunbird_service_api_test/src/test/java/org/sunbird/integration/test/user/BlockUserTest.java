package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.IssuerUtil;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.common.action.TestActionUtil;
import org.sunbird.common.action.UserUtil;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

public class BlockUserTest extends BaseCitrusTestRunner {

    public static final String TEST_BLOCK_USER_FAILURE_WITHOUT_ACCESS_TOKEN =
            "testBlockUserFailureWithoutAccessToken";
    public static final String TEST_BLOCK_USER_FAILURE_WITH_INVALID_USERID =
            "testBlockUserFailureWithInvalidUserId";
    public static final String TEST_BLOCK_USER_SUCCESS_WITH_VALID_USERID =
            "testBlockUserSuccessWithValidUserId";
    public static final String TEST_SEARCH_USER_BY_PHONE_SUCCESS =
            "testSearchUserByPhoneSuccess";
    public static final String TEST_SEARCH_USER_BY_EMAIL_SUCCESS =
            "testSearchUserByEmailSuccess";
    public static final String TEST_SEARCH_USER_BY_USER_NAME_SUCCESS =
            "testSearchUserByUserNameSuccess";
    public static final String TEST_SEARCH_USER_BY_EMPTY_FILTER_SUCCESS =
            "testSearchUserByEmptyFilterSuccess";

    public static final String TEST_SEARCH_USER_BY_LIMIT_1000_SUCCESS =
            "testSearchUserBylimit1000Success";

    public static final String TEST_SEARCH_USER_UNKNOWN_FIELDS_SUCCESS =
            "testSearchUserByUnknownFieldsSuccess";
    public static final String TEST_SEARCH_EMPTY_BODY_FAILURE =
            "testSearchUserByEmptyBodyFailure";

    public static final String TEMPLATE_DIR = "templates/user/block";
    public static final String TEMPLATE_DIR_USER_CREATE="templates/user/create";
    public static final String TEMPLATE_DIR_USER_CREATE_TEST_CASE="testCreateUserSuccess";
    private static final String GET_USER_BY_ID_SERVER_URI = "/api/user/v1/read/";
    private static final String GET_USER_BY_ID_LOCAL_URI = "/v1/user/read/";
    private String getBlockUserUrl() {

        return getLmsApiUriPath("/api/user/v1/block", "/v1/user/block");
    }
    private String getUserByLoginIdUrl() {

        return getLmsApiUriPath("/api/user/v1/profile/read", "/v1/user/getuser");
    }


    @DataProvider(name = "blockUserFailureDataProvider")
    public Object[][] blockUserFailureDataProvider() {

        return new Object[][]{
                new Object[]{
                        TEST_BLOCK_USER_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
                },
                new Object[]{
                        TEST_BLOCK_USER_FAILURE_WITH_INVALID_USERID, true, HttpStatus.NOT_FOUND
                },
        };
    }

    @DataProvider(name = "blockUserSuccessDataProvider")
    public Object[][] blockUserSuccessDataProvider() {

        return new Object[][]{
                new Object[]{
                        TEST_BLOCK_USER_SUCCESS_WITH_VALID_USERID, true, HttpStatus.OK
                },


        };
    }

    @Test(dataProvider = "blockUserFailureDataProvider")
    @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
    @CitrusTest
    public void testBlockUserFailure(
            String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
        getAuthToken(this, isAuthRequired);
        performPostTest(
                this,
                TEMPLATE_DIR,
                testName,
                getBlockUserUrl(),
                REQUEST_JSON,
                MediaType.APPLICATION_JSON,
                isAuthRequired,
                httpStatusCode,
                RESPONSE_JSON);
    }

    @Test(dataProvider = "blockUserSuccessDataProvider")
    @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
    @CitrusTest
    public void testBlockUserSuccess(
            String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
        getAuthToken(this, isAuthRequired);
        beforeTest();
        variable("userId", testContext.getVariable("userId"));
        performPostTest(
                this,
                TEMPLATE_DIR,
                testName,
                getBlockUserUrl(),
                REQUEST_JSON,
                MediaType.APPLICATION_JSON,
                isAuthRequired,
                httpStatusCode,
                RESPONSE_JSON);
    }

    private void beforeTest() {
        getAuthToken(this, true);
        String userName = Constant.USER_NAME_PREFIX + UUID.randomUUID().toString();
        testContext.setVariable("userName",userName);
        variable("username",userName);
        UserUtil.createUser(
                this, testContext, TEMPLATE_DIR_USER_CREATE, TEMPLATE_DIR_USER_CREATE_TEST_CASE, HttpStatus.OK,"$.result.userId","userId");

    }

    @Test()
    @CitrusTest
    public void testGetBlockUserByUserIdFailure() {
        performGetTest(
                this,
                TEMPLATE_DIR,
                "testGetBlockedUserByUserIdFailure",
                getLmsApiUriPath(
                        GET_USER_BY_ID_SERVER_URI,
                        GET_USER_BY_ID_LOCAL_URI,
                        TestActionUtil.getVariable(testContext, "userId")),
                true,
                HttpStatus.BAD_REQUEST,
                RESPONSE_JSON);

    }

    @Test()
    @CitrusTest
    public void testGetUserByLoginIdFailure() {
        variable("loginIdval", TestActionUtil.getVariable(testContext, "userName")+"@"+"channel_01");
        getAuthToken(this, true);
        performPostTest(
                this,
                TEMPLATE_DIR,
                "testGetBlockedUserByLoginIdFailure",
                getUserByLoginIdUrl(),
                REQUEST_JSON,
                MediaType.APPLICATION_JSON,
                true,
                HttpStatus.BAD_REQUEST,
                RESPONSE_JSON);
    }
}
