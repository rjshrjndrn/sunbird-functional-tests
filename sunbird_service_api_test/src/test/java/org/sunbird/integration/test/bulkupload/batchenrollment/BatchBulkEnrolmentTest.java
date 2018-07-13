package org.sunbird.integration.test.bulkupload.batchenrollment;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BatchBulkEnrolmentTest extends BaseCitrusTest {

  private static final String TEMPLATE_DIR = "templates/bulkupload/batchenrollment";
  private static final String BATCH_ENROLMENT_SERVER_URI = "/api/course/v1/batch/bulk/enrollment";
  private static final String BATCH_ENROLMENT_LOCAL_URI = "/v1/batch/bulk/enrollment";

  @DataProvider(name = "createBatchEnrolmentBulkUploadSuccessDataProvider")
  public Object[][] createBatchEnrolmentBulkUploadSuccessDataProvider() {
    return new Object[][] {new Object[] {"testBatchEnrollmentBulkUploadSuccess", HttpStatus.OK}};
  }

  @DataProvider(name = "createBatchEnrolmentBulkUploadFailureDataProvider")
  public Object[][] createBatchEnrolmentBulkUploadFailureDataProvider() {
    return new Object[][] {
      new Object[] {
        "testBatchEnrollmentBulkUploadFailureWithInvalidColumn", HttpStatus.BAD_REQUEST
      },
      new Object[] {"testBatchEnrollmentBulkUploadFailureWithEmptyCsvFile", HttpStatus.BAD_REQUEST},
      new Object[] {
        "testBatchEnrollmentBulkUploadFailureWithoutCsvFile", HttpStatus.INTERNAL_SERVER_ERROR
      }
    };
  }

  @Test(dataProvider = "createBatchEnrolmentBulkUploadSuccessDataProvider")
  @CitrusParameters({"testName", "status"})
  @CitrusTest
  public void testBatchEnrolmentBulkUploadSuccess(String testName, HttpStatus status) {
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getBatchBulkEnrolmentUrl(),
        REQUEST_FORM_DATA,
        status,
        RESPONSE_JSON,
        true);
  }

  @Test(dataProvider = "createBatchEnrolmentBulkUploadFailureDataProvider")
  @CitrusParameters({"testName", "status"})
  @CitrusTest
  public void testBatchEnrolmentBulkUploadFailure(String testName, HttpStatus status) {
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getBatchBulkEnrolmentUrl(),
        REQUEST_FORM_DATA,
        status,
        RESPONSE_JSON,
        true);
  }

  private String getBatchBulkEnrolmentUrl() {
    return getLmsApiUriPath(BATCH_ENROLMENT_SERVER_URI, BATCH_ENROLMENT_LOCAL_URI);
  }
}
