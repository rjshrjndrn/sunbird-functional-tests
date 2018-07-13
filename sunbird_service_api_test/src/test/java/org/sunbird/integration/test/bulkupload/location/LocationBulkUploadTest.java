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
 * @author arvind.
 */
public class LocationBulkUploadTest extends BaseCitrusTest {

  private static final String  TEMPLATE_DIR = "templates/bulkupload/location";
  private static final String LOCATION_BULK_UPLOAD_SERVER_URI="/api/data/v1/bulk/location/upload";
  private static final String LOCATION_BULK_UPLOAD_LOCAL_URI ="/v1/bulk/location/upload";

  @DataProvider(name = "stateBulkUploadSuccessDataProvider")
  public Object[][] stateBulkUploadSuccessDataProvider() {
    return new Object[][] {
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testLocationBulkUploadOfStateTypeSuccess"
        }
    };
  }

  @DataProvider(name = "stateBulkUploadFailureDataProvider")
  public Object[][] stateBulkUploadFailureDataProvider() {
    return new Object[][] {
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testLocationBulkUploadOfStateTypeFailureWithMissingMandatoryColumn",
            HttpStatus.BAD_REQUEST
        }
    };
  }

  @Test(
      dataProvider = "stateBulkUploadSuccessDataProvider"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  public void testLocationBulkUploadStateTypeSuccess(String requestFormData, String responseJson, String testName){
    getTestCase().setName(testName);
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getLocationBulkUploadUrl(),
        requestFormData,
        HttpStatus.OK,
        responseJson, true);
  }

  @Test(
      dataProvider = "stateBulkUploadFailureDataProvider"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName" , "status"})
  @CitrusTest
  public void testLocationBulkUploadStateTypeFailure(String requestFormData, String responseJson, String testName, HttpStatus status){
    getTestCase().setName(testName);
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getLocationBulkUploadUrl(),
        requestFormData,
        status,
        responseJson, true);
  }

  private String getLocationBulkUploadUrl() {
    return getLmsApiUriPath(LOCATION_BULK_UPLOAD_SERVER_URI, LOCATION_BULK_UPLOAD_LOCAL_URI);
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {
  }

}
