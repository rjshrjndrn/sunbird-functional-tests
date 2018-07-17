package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;


import javax.ws.rs.core.MediaType;

import static org.sunbird.integration.test.common.BaseCitrusTestRunner.REQUEST_JSON;

public class UserUtil {
    public static String getCreateUserUrl(BaseCitrusTestRunner runner) {
        return runner.getLmsApiUriPath("/api/user/v1/create", "/v1/user/create");
    }

    public static void createUser(
            BaseCitrusTestRunner runner,
            TestContext testContext,
            String templateDir,
            String testName,
            HttpStatus responseCode) {
        runner.http(
                builder ->
                        TestActionUtil.getPostRequestTestAction(
                                testContext,
                                builder,
                                Constant.LMS_ENDPOINT,
                                testName,
                                templateDir,
                                getCreateUserUrl(runner),
                                MediaType.APPLICATION_JSON,
                                REQUEST_JSON,
                                org.sunbird.integration.test.common.TestActionUtil.getHeaders(false)
                                ));
        runner.http(
                builder ->
                        TestActionUtil.getExtractFromResponseTestAction(
                                testContext,
                                builder,
                                Constant.LMS_ENDPOINT,
                                responseCode,
                                "$.result.userId",
                                "userId"));
    }
}
