package org.sunbird.integration.test.bulkupload.location;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.annotation.CleanUp;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Class contains functional test cases for location bulk upload.
 * @author arvind.
 */
public class LocationBulkUploadTest extends BaseCitrusTest {

  private static final String BULK_UPLOAD_LOCATION_URI = "/api/data/v1/bulk/location/upload";
  private static final String  TEST_DIR_BULK_UPLOAD_LOCATION_SUCCESS = "templates/location/bulkupload/state/success/";
  private static final String  TEST_DIR_BULK_UPLOAD_LOCATION_FAILURE = "templates/location/bulkupload/state/failure/";

  public static final String REQUEST_FORM_DATA = "request.params";
  public static final String RESPONSE_JSON = "response.json";

  @Autowired
  private HttpClient restTestClient;
  @Autowired private TestGlobalProperty initGlobalValues;
  private ObjectMapper objectMapper = new ObjectMapper();

  @DataProvider(name = "stateBulkUploadDataProvider")
  public Object[][] stateBulkUploadDataProvider() {
    return new Object[][] {
        new Object[]{
            TEST_DIR_BULK_UPLOAD_LOCATION_SUCCESS + REQUEST_FORM_DATA,
            TEST_DIR_BULK_UPLOAD_LOCATION_SUCCESS + RESPONSE_JSON,
            "stateBulkUploadSuccess"
        },
        new Object[]{
            TEST_DIR_BULK_UPLOAD_LOCATION_FAILURE + REQUEST_FORM_DATA,
            TEST_DIR_BULK_UPLOAD_LOCATION_FAILURE + RESPONSE_JSON,
            "stateBulkUploadWithoutMandatoryParams"
        }
    };
  }

  @Test(
      dataProvider = "stateBulkUploadDataProvider"
  )
  @CitrusParameters({"requestJson", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate the functional test cases for the state type location bulk upload. It include scenarios -
   * 1.State upload with valid file. Expecting success response with processId.
   * 2.Upload file without mandatory fields and expecting BAD_REQUEST in response with error message as mandatory parameter are missing.
   */
  public void testStateBulkUpload(String requestFormData, String responseJson, String testName){
    getTestCase().setName(testName);
    if ((TEST_DIR_BULK_UPLOAD_LOCATION_SUCCESS + RESPONSE_JSON)
        .equals(responseJson)) {
      String testFolderPath = TEST_DIR_BULK_UPLOAD_LOCATION_SUCCESS;
      new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues, BULK_UPLOAD_LOCATION_URI, requestFormData, testFolderPath);

      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.OK)
          .payload(new ClassPathResource(responseJson));
    } else {
      String testFolderPath = TEST_DIR_BULK_UPLOAD_LOCATION_FAILURE;
      new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues, BULK_UPLOAD_LOCATION_URI, requestFormData, testFolderPath);

      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    }
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {
  }

}
