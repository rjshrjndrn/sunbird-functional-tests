package org.sunbird.integration.test.bulkupload.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author arvind.
 */
public class UserBulkUploadTest extends BaseCitrusTest{

  private static final String  TEMPLATE_DIR = "templates/bulkupload/user";
  private static final String USER_BULK_UPLOAD_SERVER_URI="/api/user/v1/upload";
  private static final String USER_BULK_UPLOAD_LOCAL_URI ="/v1/user/upload";

  @DataProvider(name = "userBulkUploadSuccessDataProvider")
  public Object[][] userBulkUploadSuccessDataProvider() {
    return new Object[][] {
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testUserBulkUploadSuccess"
        }
    };
  }

  @DataProvider(name = "userBulkUploadFailureDataProvider")
  public Object[][] userBulkUploadFailureDataProvider() {
    return new Object[][] {
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testUserBulkUploadFailureWithInvalidColumn",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testUserBulkUploadFailureWithEmptyCsvFile",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testUserBulkUploadFailureWithoutCsvFile",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testUserBulkUploadFailureWithoutOrgDetails",
            HttpStatus.BAD_REQUEST
        }
    };
  }

  @Test(
      dataProvider = "userBulkUploadSuccessDataProvider"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  public void testUserBulkUploadSuccess(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getUserBulkUploadUrl(),
        requestFormData,
        HttpStatus.OK,
        responseJson, true);

  }

  @Test(
      dataProvider = "userBulkUploadFailureDataProvider"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName" , "status"})
  @CitrusTest
  public void testUserBulkUploadFailure(String requestFormData, String responseJson, String testName, HttpStatus status) {
    getTestCase().setName(testName);
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getUserBulkUploadUrl(),
        requestFormData,
        status,
        responseJson, true);
  }

  private String getUserBulkUploadUrl() {
    return getLmsApiUriPath(USER_BULK_UPLOAD_SERVER_URI, USER_BULK_UPLOAD_LOCAL_URI);
  }

}
