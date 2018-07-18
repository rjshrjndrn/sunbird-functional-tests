package org.sunbird.common.action;

import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;

public class UserBlockUtil {

    public static String getCreateUserUrl(BaseCitrusTestRunner runner) {
        return runner.getLmsApiUriPath("/api/user/v1/create", "/v1/user/create");
    }


}
