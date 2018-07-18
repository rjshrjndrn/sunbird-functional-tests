package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class CreateUserTest extends BaseCitrusTestRunner {

    public static final String TEST_NAME_CREATE_USER_FAILURE_WITH_DUPLICATE_COMBINATION_EXTERNAL_ID_EXTERNAL_ID_TYPE_AND_EXTERNAL_ID_PROVIDER =
            "testCreateUserFailureWithDuplicateExternalIdExternalIdTypeExternalIdProvider";
    public static final String TEST_NAME_CREATE_USER_FAILURE_WITH_DUPLICATE_COMBINATION_OF_EXTERNAL_ID_TYPE_AND_EXTERNAL_ID_PROVIDER =
            "testCreateUserFailureWithDuplicateCombinationOfExternalIdTypeAndExternalIdProvider";
    public static final String TEST_NAME_CREATE_USER_SUCESS_WITH_VALID_EXTERNAL_ID_EXTERNAL_PROVIDER_ID_AND_EXTERNAL_PROVIDER_TYPE =
            "testCreateUserSucessWithUniqueExternalIdExternalProviderIdExternalProviderType";
//    public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_CHANNEL =
//            "testCreateSubOrgFailureWithInvalidChannel";
//    public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_EXTERNAL_ID_WITHOUT_PROVIDER =
//            "testCreateSubOrgFailureWithExternalIdWithoutProvider";
//    public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITHOUT_ACCESS_TOKEN =
//            "testCreateSubOrgFailureWithoutAccessToken";

    public static final String TEMPLATE_DIR = "templates/user/create";

    private String getCreateUserUrl() {

        return getLmsApiUriPath("/api/user/v1/create", "/v1/user/create");
    }

    @DataProvider(name = "createUserFailureDataProvider")
    public Object[][] createOrgFailureDataProvider() {

        return new Object[][]{
                new Object[]{TEST_NAME_CREATE_USER_FAILURE_WITH_DUPLICATE_COMBINATION_EXTERNAL_ID_EXTERNAL_ID_TYPE_AND_EXTERNAL_ID_PROVIDER, false, HttpStatus.BAD_REQUEST},
                new Object[]{
                        TEST_NAME_CREATE_USER_FAILURE_WITH_DUPLICATE_COMBINATION_OF_EXTERNAL_ID_TYPE_AND_EXTERNAL_ID_PROVIDER,
                        false,
                        HttpStatus.BAD_REQUEST
                },
                new Object[]{
                        TEST_NAME_CREATE_USER_SUCESS_WITH_VALID_EXTERNAL_ID_EXTERNAL_PROVIDER_ID_AND_EXTERNAL_PROVIDER_TYPE, false, HttpStatus.OK
                }
        };
    }

    @Test(dataProvider = "createUserFailureDataProvider")
    @CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
    @CitrusTest
    public void testCreateUser(
            String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

        performPostTest(
                this,
                TEMPLATE_DIR,
                testName,
                getCreateUserUrl(),
                REQUEST_JSON,
                MediaType.APPLICATION_JSON,
                isAuthRequired,
                httpStatusCode,
                RESPONSE_JSON);
    }

}
