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

  private static final String  TEMPLATE_DIR = "templates/bulkupload/organisation";
  private static final String BULK_UPLOAD_ORGANISATION_SERVER_URI="/api/org/v1/upload";
  private static final String BULK_UPLOAD_ORGANISATION_LOCAL_URI ="/v1/org/upload";

  @Autowired private TestGlobalProperty initGlobalValues;

  @DataProvider(name = "organisationBulkUploadSuccessDataProvider")
  public Object[][] organisationBulkUploadSuccessDataProvider() {
    return new Object[][] {
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testOrgBulkUploadSuccess"
        }
    };
  }

  @DataProvider(name = "organisationBulkUploadFailureDataProvider")
  public Object[][] organisationBulkUploadFailureDataProvider() {
    return new Object[][] {
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testOrgBulkUploadFailureWithInvalidColumn",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testOrgBulkUploadFailureWithEmptyCsvFile",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            REQUEST_FORM_DATA,
            RESPONSE_JSON,
            "testOrgBulkUploadFailureWithoutCsvFile",
            HttpStatus.INTERNAL_SERVER_ERROR
        }
    };
  }

  @Test(
      dataProvider = "organisationBulkUploadSuccessDataProvider"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  public void testOrgBulkUploadSuccess(String requestFormData, String responseJson, String testName) {
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getOrgBulkUploadUrl(),
        requestFormData,
        HttpStatus.OK,
        responseJson, true);
  }

  @Test(
      dataProvider = "organisationBulkUploadFailureDataProvider"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName", "status"})
  @CitrusTest
  public void testOrgBulkUploadFailure(String requestFormData, String responseJson, String testName, HttpStatus status) {
    getTestCase().setName(testName);
    performMultipartTest(
        testName,
        TEMPLATE_DIR,
        getOrgBulkUploadUrl(),
        requestFormData,
        status,
        responseJson, true);

  }

  private String getOrgBulkUploadUrl() {
    return getLmsApiUriPath(BULK_UPLOAD_ORGANISATION_SERVER_URI, BULK_UPLOAD_ORGANISATION_LOCAL_URI);
  }

}
