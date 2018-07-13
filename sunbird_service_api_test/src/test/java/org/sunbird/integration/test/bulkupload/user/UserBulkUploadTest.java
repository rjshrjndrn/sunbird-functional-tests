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

public class UserBulkUploadTest extends BaseCitrusTest{

  private static final String  TEMPLATE_DIR = "templates/bulkupload/user";
  private static final String USER_BULK_UPLOAD_SERVER_URI="/api/user/v1/upload";
  private static final String USER_BULK_UPLOAD_LOCAL_URI ="/v1/user/upload";


  @DataProvider(name = "userBulkUploadSuccessDataProvider")
  public Object[][] userBulkUploadSuccessDataProvider() {
    return new Object[][] {
        new Object[]{
            "testUserBulkUploadSuccess"
        }
    };
  }

  @DataProvider(name = "userBulkUploadFailureDataProvider")
  public Object[][] userBulkUploadFailureDataProvider() {
    return new Object[][] {
        new Object[]{
            "testUserBulkUploadFailureWithInvalidColumn",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            "testUserBulkUploadFailureWithEmptyCsvFile",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            "testUserBulkUploadFailureWithoutCsvFile",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            "testUserBulkUploadFailureWithoutOrgDetails",
            HttpStatus.BAD_REQUEST
        }
    };
  }

  @Test(
      dataProvider = "userBulkUploadSuccessDataProvider"
  )
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testUserBulkUploadSuccess(String testName) {
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getUserBulkUploadUrl(),
        REQUEST_FORM_DATA,
        HttpStatus.OK,
        RESPONSE_JSON, true);

  }

  @Test(
      dataProvider = "userBulkUploadFailureDataProvider"
  )
  @CitrusParameters({"testName" , "status"})
  @CitrusTest
  public void testUserBulkUploadFailure(String testName, HttpStatus status) {
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getUserBulkUploadUrl(),
        REQUEST_FORM_DATA,
        status,
        RESPONSE_JSON, true);
  }

  private String getUserBulkUploadUrl() {
    return getLmsApiUriPath(USER_BULK_UPLOAD_SERVER_URI, USER_BULK_UPLOAD_LOCAL_URI);
  }

}
