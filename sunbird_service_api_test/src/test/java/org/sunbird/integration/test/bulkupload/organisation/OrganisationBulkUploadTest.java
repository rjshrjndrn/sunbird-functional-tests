package org.sunbird.integration.test.bulkupload.organisation;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OrganisationBulkUploadTest extends BaseCitrusTestRunner {

  private static final String  TEMPLATE_DIR = "templates/bulkupload/organisation";
  private static final String BULK_UPLOAD_ORGANISATION_SERVER_URI="/api/org/v1/upload";
  private static final String BULK_UPLOAD_ORGANISATION_LOCAL_URI ="/v1/org/upload";

  @DataProvider(name = "organisationBulkUploadSuccessDataProvider")
  public Object[][] organisationBulkUploadSuccessDataProvider() {
    return new Object[][] {
        new Object[]{
            "testOrgBulkUploadSuccess"
        }
    };
  }

  @DataProvider(name = "organisationBulkUploadFailureDataProvider")
  public Object[][] organisationBulkUploadFailureDataProvider() {
    return new Object[][] {
        new Object[]{
            "testOrgBulkUploadFailureWithInvalidColumn",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            "testOrgBulkUploadFailureWithEmptyCsvFile",
            HttpStatus.BAD_REQUEST
        },
        new Object[]{
            "testOrgBulkUploadFailureWithoutCsvFile",
            HttpStatus.INTERNAL_SERVER_ERROR
        }
    };
  }

  @Test(
      dataProvider = "organisationBulkUploadSuccessDataProvider"
  )
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testOrgBulkUploadSuccess(String testName) {
    performMultipartTest(
        this,
        TEMPLATE_DIR,
        testName,
        getOrgBulkUploadUrl(),
        REQUEST_FORM_DATA,
        null,
        true,
        HttpStatus.OK,
        RESPONSE_JSON);
  }

  @Test(
      dataProvider = "organisationBulkUploadFailureDataProvider"
  )
  @CitrusParameters({"testName", "status"})
  @CitrusTest
  public void testOrgBulkUploadFailure(String testName, HttpStatus status) {
    performMultipartTest(
        this,
        TEMPLATE_DIR,
        testName,
        getOrgBulkUploadUrl(),
        REQUEST_FORM_DATA,
        null,
        true,
        status,
        RESPONSE_JSON);

  }

  private String getOrgBulkUploadUrl() {
    return getLmsApiUriPath(BULK_UPLOAD_ORGANISATION_SERVER_URI, BULK_UPLOAD_ORGANISATION_LOCAL_URI);
  }

}
