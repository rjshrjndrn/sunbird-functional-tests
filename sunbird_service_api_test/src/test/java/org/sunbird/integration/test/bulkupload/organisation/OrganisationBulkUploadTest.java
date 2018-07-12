package org.sunbird.integration.test.bulkupload.organisation;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author arvind
 */
public class OrganisationBulkUploadTest extends BaseCitrusTest {

  private static final String  TEST_DIR_BULK_UPLOAD_ORG_SUCCESS = "templates/bulkupload/organisation/success";
  private static final String  TEST_DIR_BULK_UPLOAD_ORG_FAILURE = "templates/bulkupload/organisation/failure";

  @Autowired private TestGlobalProperty initGlobalValues;

  @DataProvider(name = "orgBulkUploadSuccess")
  public Object[][] orgBulkUploadSuccess() {
    return new Object[][] {
        new Object[]{
            "request-with-valid-fields.params",
            "successResponse.json",
            "orgBulkUploadWithValidFields"
        }
    };
  }

  @DataProvider(name = "orgBulkUploadFailureInvalidFields")
  public Object[][] orgBulkUploadFailureInvalidFields() {
    return new Object[][] {
        new Object[]{
            "request-with-invalid-fields.params",
            "failureResponse.json",
            "orgBulkUploadWithInvalidFields"
        }
    };
  }

  @DataProvider(name = "orgBulkUploadFailureEmptyFile")
  public Object[][] orgBulkUploadFailureEmptyFile() {
    return new Object[][] {
        new Object[]{
            "request-with-empty-file.params",
            "emptyFileFailureResponse.json",
            "orgBulkUploadWithEmptyFile"
        }
    };
  }

  @DataProvider(name = "orgBulkUploadFailureFileAbsent")
  public Object[][] orgBulkUploadFailureFileAbsent() {
    return new Object[][] {
        new Object[]{
            "request-without-file.params",
            "fileAbsentFailureResponse.json",
            "orgBulkUploadWithFileAbsent"
        }
    };
  }


  @Test(
      dataProvider = "orgBulkUploadSuccess"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate the functional test cases for organisation bulk upload for success scenario -
   * 1.upload csv file with fields : orgName,isRootOrg,channel,externalId,provider,description,homeUrl,orgCode,orgType,preferredLanguage,contactDetail
   */
  public void testOrgBulkUploadSuccess(String requestFormData, String responseJson, String testName) {
    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_ORG_SUCCESS,
        getOrgBulkUploadUrl(),
        requestFormData,
        HttpStatus.OK,
        responseJson, true);

  }

  @Test(
      dataProvider = "orgBulkUploadFailureInvalidFields"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for organisation bulk upload for failure scenario -
   * 1.upload csv file with invalid fields and expecting BAD request in response.
   */
  public void testOrgBulkUploadFailureInvalidFields(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_ORG_FAILURE,
        getOrgBulkUploadUrl(),
        requestFormData,
        HttpStatus.BAD_REQUEST,
        responseJson, true);

  }

  @Test(
      dataProvider = "orgBulkUploadFailureFileAbsent"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for organisation bulk upload for failure scenario -
   * 1.call api without attaching csv file and excepting server error exception. //TODO: need to correct the code to throw CLIENT_ERROR
   */
  public void testOrgBulkUploadFailureFileAbsent(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_ORG_FAILURE,
        getOrgBulkUploadUrl(),
        requestFormData,
        HttpStatus.INTERNAL_SERVER_ERROR,
        responseJson, true);

  }

  @Test(
      dataProvider = "orgBulkUploadFailureEmptyFile"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for organisation bulk upload for failure scenario -
   * 1.upload csv file with empty file and expecting BAD request in response.
   */
  public void testOrgBulkUploadFailureEmptyFile(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BULK_UPLOAD_ORG_FAILURE,
        getOrgBulkUploadUrl(),
        requestFormData,
        HttpStatus.BAD_REQUEST,
        responseJson, true);
  }

  private String getOrgBulkUploadUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/org/upload"
        : "/api/org/v1/upload";
  }


}
