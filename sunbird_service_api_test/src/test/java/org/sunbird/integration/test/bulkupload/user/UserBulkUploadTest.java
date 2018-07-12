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

  private static final String  TEST_DIR_BULK_UPLOAD_USER_SUCCESS = "templates/bulkupload/user/success";
  private static final String  TEST_DIR_BULK_UPLOAD_USER_FAILURE = "templates/bulkupload/user/failure";

  @Autowired private TestGlobalProperty initGlobalValues;
  @DataProvider(name = "userBulkUploadSuccess")
  public Object[][] userBulkUploadSuccess() {
    return new Object[][] {
        new Object[]{
            "request-with-valid-fields.params",
            "successResponse.json",
            "userBulkUploadWithValidFields"
        }
    };
  }

  @DataProvider(name = "userBulkUploadFailureInvalidFields")
  public Object[][] userBulkUploadFailureInvalidFields() {
    return new Object[][] {
        new Object[]{
            "request-with-invalid-fields.params",
            "invalidColumnsFailureResponse.json",
            "userBulkUploadWithInvalidFields"
        }
    };
  }

  @DataProvider(name = "userBulkUploadFailureEmptyFile")
  public Object[][] userBulkUploadFailureEmptyFile() {
    return new Object[][] {
        new Object[]{
            "request-with-empty-file.params",
            "emptyFileFailureResponse.json",
            "userBulkUploadWithEmptyFile"
        }
    };
  }

  @DataProvider(name = "userBulkUploadFailureFileAbsent")
  public Object[][] userBulkUploadFailureFileAsent() {
    return new Object[][] {
        new Object[]{
            "request-without-file.params",
            "fileAbsentFailureResponse.json",
            "userBulkUploadWithoutFile"
        }
    };
  }

  @DataProvider(name = "userBulkUploadFailureOrgDetailMissing")
  public Object[][] userBulkUploadFailureOrgDetailMissing() {
    return new Object[][] {
        new Object[]{
            "request-without-org-detail.params",
            "requiredParamMissingFailureResponse.json",
            "userBulkUploadWithoutOrgDetail"
        }
    };
  }

  @Test(
      dataProvider = "userBulkUploadSuccess"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate the functional test cases for user bulk upload for success scenario -
   * 1.upload csv file with fields : orgName,isRootOrg,channel,externalId,provider,description,homeUrl,orgCode,orgType,preferredLanguage,contactDetail
   */
  public void testOrgBulkUploadSuccess(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);
    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_USER_SUCCESS,
        getUserBulkUploadUrl(),
        requestFormData,
        HttpStatus.OK,
        responseJson, true);

  }

  @Test(
      dataProvider = "userBulkUploadFailureInvalidFields"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for user bulk upload for failure scenario -
   * 1.upload csv file with invalid fields and expecting BAD request in response.
   */
  public void testOrgBulkUploadFailureWithInvalidFields(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_USER_FAILURE,
        getUserBulkUploadUrl(),
        requestFormData,
        HttpStatus.BAD_REQUEST,
        responseJson, true);
  }

  @Test(
      dataProvider = "userBulkUploadFailureEmptyFile"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for user bulk upload for failure scenario -
   * 1.upload csv file with empty file and expecting BAD request in response.
   */
  public void testOrgBulkUploadFailureWithEmptyFile(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_USER_FAILURE,
        getUserBulkUploadUrl(),
        requestFormData,
        HttpStatus.BAD_REQUEST,
        responseJson, true);
  }

  @Test(
      dataProvider = "userBulkUploadFailureFileAbsent"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for user bulk upload for failure scenario -
   * 1.call api without attaching csv file and excepting server error exception.
   */
  public void testOrgBulkUploadFailureWithoutFile(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_USER_FAILURE,
        getUserBulkUploadUrl(),
        requestFormData,
        HttpStatus.BAD_REQUEST,
        responseJson, true);
  }

  @Test(
      dataProvider = "userBulkUploadFailureOrgDetailMissing"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for user bulk upload for failure scenario -
   * 1.upload csv file with valid file and org details are missing, and expecting BAD_REQUEST.
   */
  public void testOrgBulkUploadFailureWithoutOrgDetails(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_USER_FAILURE,
        getUserBulkUploadUrl(),
        requestFormData,
        HttpStatus.BAD_REQUEST,
        responseJson, true);
  }

  private String getUserBulkUploadUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/user/upload"
        : "/api/user/v1/upload";
  }

}
