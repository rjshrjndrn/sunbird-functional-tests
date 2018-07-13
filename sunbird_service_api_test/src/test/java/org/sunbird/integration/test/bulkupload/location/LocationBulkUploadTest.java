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

public class LocationBulkUploadTest extends BaseCitrusTest {

  private static final String  TEMPLATE_DIR = "templates/bulkupload/location";
  private static final String LOCATION_BULK_UPLOAD_SERVER_URI="/api/data/v1/bulk/location/upload";
  private static final String LOCATION_BULK_UPLOAD_LOCAL_URI ="/v1/bulk/location/upload";


  @DataProvider(name = "stateBulkUploadSuccessDataProvider")
  public Object[][] stateBulkUploadSuccessDataProvider() {
    return new Object[][] {
        new Object[]{
            "testLocationBulkUploadOfStateTypeSuccess"
        }
    };
  }

  @DataProvider(name = "stateBulkUploadFailureDataProvider")
  public Object[][] stateBulkUploadFailureDataProvider() {
    return new Object[][] {
        new Object[]{
            "testLocationBulkUploadOfStateTypeFailureWithMissingMandatoryColumn",
            HttpStatus.BAD_REQUEST
        }
    };
  }

  @Test(
      dataProvider = "stateBulkUploadSuccessDataProvider"
  )
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testLocationBulkUploadStateTypeSuccess(String testName){
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getLocationBulkUploadUrl(),
        REQUEST_FORM_DATA,
        HttpStatus.OK,
        RESPONSE_JSON, true);
  }

  @Test(
      dataProvider = "stateBulkUploadFailureDataProvider"
  )
  @CitrusParameters({"testName" , "status"})
  @CitrusTest
  public void testLocationBulkUploadStateTypeFailure(String testName, HttpStatus status){
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getLocationBulkUploadUrl(),
        REQUEST_FORM_DATA,
        status,
        RESPONSE_JSON, true);
  }

  private String getLocationBulkUploadUrl() {
    return getLmsApiUriPath(LOCATION_BULK_UPLOAD_SERVER_URI, LOCATION_BULK_UPLOAD_LOCAL_URI);
  }

  @CleanUp
  /** Method to perform the cleanup after test suite completion. */
  public static void cleanUp() {
  }

}
