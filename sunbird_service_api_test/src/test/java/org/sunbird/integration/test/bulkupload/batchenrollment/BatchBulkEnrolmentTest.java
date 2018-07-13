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

  private static final String  TEMPLATE_DIR = "templates/bulkupload/batchenrollment";
  private static final String BATCH_ENROLMENT_SERVER_URI="/api/course/v1/batch/bulk/enrollment";
  private static final String BATCH_ENROLMENT_LOCAL_URI ="/v1/batch/bulk/enrollment";

  @DataProvider(name = "createBatchEnrolmentBulkUploadSuccessDataProvider")
  public Object[][] createBatchEnrolmentBulkUploadSuccessDataProvider() {
    return new Object[][] {
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testBatchEnrollmentBulkUploadSuccess",
            HttpStatus.OK
        }
    };
  }

  @DataProvider(name = "createBatchEnrolmentBulkUploadFailureDataProvider")
  public Object[][] createBatchEnrolmentBulkUploadFailureDataProvider() {
    return new Object[][] {
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testBatchEnrollmentBulkUploadFailureWithInvalidColumn",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testBatchEnrollmentBulkUploadFailureWithEmptyCsvFile",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testBatchEnrollmentBulkUploadFailureWithoutCsvFile",
            HttpStatus.INTERNAL_SERVER_ERROR
        }

    };
  }

  @Test(
      dataProvider = "createBatchEnrolmentBulkUploadSuccessDataProvider"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName", "status"})
  @CitrusTest
  public void testBatchEnrolmentBulkUploadSuccess(String requestFormData, String responseJson, String testName, HttpStatus status) {
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getBatchBulkEnrolmentUrl(),
        requestFormData,
        status,
        responseJson, true);

  }

  @Test(
      dataProvider = "createBatchEnrolmentBulkUploadFailureDataProvider"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName" , "status"})
  @CitrusTest
  public void testBatchEnrolmentBulkUploadFailure(String requestFormData, String responseJson, String testName, HttpStatus status) {
    getTestCase().setName(testName);

    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getBatchBulkEnrolmentUrl(),
        requestFormData,
        status,
        responseJson, true);
  }

  private String getBatchBulkEnrolmentUrl() {
    return getLmsApiUriPath(BATCH_ENROLMENT_SERVER_URI, BATCH_ENROLMENT_LOCAL_URI);
  }
}
