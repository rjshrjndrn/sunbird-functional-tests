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

  private static final String  TEST_DIR_BATCH_BULK_ENROLMENT_SUCCESS = "templates/bulkupload/coursebatch/success/";
  private static final String  TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE = "templates/bulkupload/coursebatch/failure/";
  private static final String  TEST_DIR_BATCH_BULK_ENROLMENT_RESPONSE = "templates/bulkupload/coursebatch/response/";

  @Autowired
  private HttpClient restTestClient;
  @Autowired private TestGlobalProperty initGlobalValues;

  @DataProvider(name = "batchBulkEnrolmentSuccess")
  public Object[][] batchBulkEnrolmentSuccess() {
    return new Object[][] {
        new Object[]{
            TEST_DIR_BATCH_BULK_ENROLMENT_SUCCESS + "request-with-valid-fields.params",
            TEST_DIR_BATCH_BULK_ENROLMENT_RESPONSE + "successResponse.json",
            "batchBulkEnrolmentWithValidFields"
        }
    };
  }

  @DataProvider(name = "batchBulkEnrolmentFailure")
  public Object[][] batchBulkEnrolmentFailure() {
    return new Object[][] {
        new Object[]{
            TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE + "request-with-invalid-fields.params",
            TEST_DIR_BATCH_BULK_ENROLMENT_RESPONSE + "failureResponse.json",
            "batchBulkEnrolmentWithInvalidFields"
        },
        new Object[]{
            TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE + "request-with-empty-file.params",
            TEST_DIR_BATCH_BULK_ENROLMENT_RESPONSE + "emptyFileFailureResponse.json",
            "batchBulkEnrolmentWithEmptyFile"
        },
        new Object[]{
            TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE + "request-without-file.params",
            TEST_DIR_BATCH_BULK_ENROLMENT_RESPONSE + "fileAbsentFailureResponse.json",
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
    getTestCase().setName(testName);
    String testFolderPath;
    testFolderPath = TEST_DIR_BATCH_BULK_ENROLMENT_SUCCESS;
    new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues,
        getBatchBulkEnrolmentUrl(), requestFormData, testFolderPath, getHeaderWithAuthToken());

    http()
        .client(restTestClient)
        .receive()
        .response(HttpStatus.OK)
        .payload(new ClassPathResource(responseJson));

  }

  @Test(
      dataProvider = "batchBulkEnrolmentFailure"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for batch bulk enrolment for various failure scenarios -
   * 1.upload csv file with invalid fields and expecting BAD request in response.
   * 2.upload csv file with empty file and expecting BAD request in response.
   * 3.call api without attaching csv file and excepting server error exception. //TODO: need to correct the code to throw CLIENT_ERROR
   */
  public void testBatchBulkEnrolmentFailureCases(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);
    String testFolderPath = TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE;

    if ((TEST_DIR_BATCH_BULK_ENROLMENT_RESPONSE + "failureResponse.json")
        .equals(responseJson)) {
      new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues,
          getBatchBulkEnrolmentUrl(), requestFormData, testFolderPath , getHeaderWithAuthToken());

      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    }else if((TEST_DIR_BATCH_BULK_ENROLMENT_RESPONSE + "emptyFileFailureResponse.json").equals(responseJson)){
      new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues,
          getBatchBulkEnrolmentUrl(), requestFormData, testFolderPath, getHeaderWithAuthToken());

      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    }else if((TEST_DIR_BATCH_BULK_ENROLMENT_RESPONSE + "fileAbsentFailureResponse.json").equals(responseJson)){
      testFolderPath = TEST_DIR_BATCH_BULK_ENROLMENT_FAILURE;
      new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues,
          getBatchBulkEnrolmentUrl(), requestFormData, testFolderPath, getHeaderWithAuthToken());

      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.INTERNAL_SERVER_ERROR)
          .payload(new ClassPathResource(responseJson));
    }
  }

  private String getBatchBulkEnrolmentUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/batch/bulk/enrollment"
        : "/api/course/v1/batch/bulk/enrollment";
  }
}
