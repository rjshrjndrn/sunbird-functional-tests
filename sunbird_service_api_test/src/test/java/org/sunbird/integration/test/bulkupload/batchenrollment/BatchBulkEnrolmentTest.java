package org.sunbird.integration.test.bulkupload.batchenrollment;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author arvind
 */
public class BatchBulkEnrolmentTest extends BaseCitrusTest {

  private static final String  TEST_DIR_BATCH_BULK_ENROLMENT_SUCCESS = "templates/bulkupload/coursebatch/success";
  private static final String  TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE = "templates/bulkupload/coursebatch/failure";

  @Autowired private TestGlobalProperty initGlobalValues;

  @DataProvider(name = "batchBulkEnrolmentSuccess")
  public Object[][] batchBulkEnrolmentSuccess() {
    return new Object[][] {
        new Object[]{
            "request-with-valid-fields.params",
            "successResponse.json",
            "batchBulkEnrolmentWithValidFields"
        }
    };
  }

  @DataProvider(name = "batchBulkEnrolmentFailureInvalidFields")
  public Object[][] batchBulkEnrolmentFailureInvalidFields() {
    return new Object[][] {
        new Object[]{
            "request-with-invalid-fields.params",
            "failureResponse.json",
            "batchBulkEnrolmentWithInvalidFields"
        }
    };
  }

  @DataProvider(name = "batchBulkEnrolmentFailureEmptyFile")
  public Object[][] batchBulkEnrolmentFailureEmptyFile() {
    return new Object[][] {
        new Object[]{
            "request-with-empty-file.params",
            "emptyFileFailureResponse.json",
            "batchBulkEnrolmentWithEmptyFile"
        }
    };
  }

  @DataProvider(name = "batchBulkEnrolmentFailureFileAbsent")
  public Object[][] batchBulkEnrolmentFailureFileAbsent() {
    return new Object[][] {
        new Object[]{
            "request-without-file.params",
            "fileAbsentFailureResponse.json",
            "batchBulkEnrolmentWithoutFile"
        },
    };
  }

  @Test(
      dataProvider = "batchBulkEnrolmentSuccess"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate the functional test cases for batch bulk enrolment for success scenario -
   * 1.upload csv file with fields batchId,userIds.
   */
  public void testBatchBulkEnrolmentSuccess(String requestFormData, String responseJson, String testName) {
    performMultipartTest(
        testName,
        TEST_DIR_BATCH_BULK_ENROLMENT_SUCCESS,
        getBatchBulkEnrolmentUrl(),
        requestFormData,
        HttpStatus.OK,
        responseJson, true);

  }

  @Test(
      dataProvider = "batchBulkEnrolmentFailureInvalidFields"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /*
   * Method to validate functional test cases for batch bulk enrolment for failure scenarios-
   * 1.upload csv file with invalid fields and expecting BAD request in response.
   */
  public void testBatchBulkEnrolmentFailureInvalidFields(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE,
        getBatchBulkEnrolmentUrl(),
        requestFormData,
        HttpStatus.BAD_REQUEST,
        responseJson, true);
  }

  @Test(
      dataProvider = "batchBulkEnrolmentFailureEmptyFile"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /*
   * Method to validate functional test cases for batch bulk enrolment for failure scenario -
   * 1.upload csv file with empty file and expecting BAD request in response.
   */
  public void testBatchBulkEnrolmentFailureEmptyFile(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE,
        getBatchBulkEnrolmentUrl(),
        requestFormData,
        HttpStatus.BAD_REQUEST,
        responseJson, true);
  }

  @Test(
      dataProvider = "batchBulkEnrolmentFailureFileAbsent"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /*
   * Method to validate functional test cases for batch bulk enrolment for failure scenario -
   * 1.call api without attaching csv file and excepting server error exception. //TODO: need to correct the code to throw CLIENT_ERROR
   */
  public void testBatchBulkEnrolmentFailureFileAbsent(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);
    performMultipartTest(
        testName,
        TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE,
        getBatchBulkEnrolmentUrl(),
        requestFormData,
        HttpStatus.INTERNAL_SERVER_ERROR,
        responseJson, true);
  }

  private String getBatchBulkEnrolmentUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/batch/bulk/enrollment"
        : "/api/course/v1/batch/bulk/enrollment";
  }
}
