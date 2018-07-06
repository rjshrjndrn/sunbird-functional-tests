package org.sunbird.integration.test.bulkupload.organisation;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class OrganisationBulkUploadTest extends BaseCitrusTest {

  private static final String  TEST_DIR_BULK_UPLOAD_ORG_SUCCESS = "templates/bulkupload/organisation/success/";
  private static final String  TEST_DIR_BULK_UPLOAD_ORG_FAILURE = "templates/bulkupload/organisation/failure/";
  private static final String  TEST_DIR_BULK_UPLOAD_ORG_RESPONSE = "templates/bulkupload/organisation/response/";


  public static final String REQUEST_FORM_DATA = "request.params";
  public static final String RESPONSE_JSON = "response.json";

  @Autowired
  private HttpClient restTestClient;
  @Autowired private TestGlobalProperty initGlobalValues;
  private ObjectMapper objectMapper = new ObjectMapper();

  @DataProvider(name = "orgBulkUploadSuccess")
  public Object[][] orgBulkUploadSuccess() {
    return new Object[][] {
        new Object[]{
            TEST_DIR_BULK_UPLOAD_ORG_SUCCESS + "request-with-valid-fields.params",
            TEST_DIR_BULK_UPLOAD_ORG_RESPONSE + "successResponse.json",
            "orgBulkUploadWithValidFields"
        }
    };
  }

  @DataProvider(name = "orgBulkUploadFailure")
  public Object[][] orgBulkUploadFailure() {
    return new Object[][] {
        new Object[]{
            TEST_DIR_BULK_UPLOAD_ORG_FAILURE + "request-with-invalid-fields.params",
            TEST_DIR_BULK_UPLOAD_ORG_RESPONSE + "failureResponse.json",
            "orgBulkUploadWithInvalidFields"
        },
        new Object[]{
            TEST_DIR_BULK_UPLOAD_ORG_FAILURE + "request-with-empty-file.params",
            TEST_DIR_BULK_UPLOAD_ORG_RESPONSE + "emptyFileFailureResponse.json",
            "orgBulkUploadWithEmptyFile"
        },
        new Object[]{
            TEST_DIR_BULK_UPLOAD_ORG_FAILURE + "request-without-file.params",
            TEST_DIR_BULK_UPLOAD_ORG_RESPONSE + "fileAbsentFailureResponse.json",
            "orgBulkUploadWithoutFile"
        },
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
    getTestCase().setName(testName);
    String testFolderPath;
    testFolderPath = TEST_DIR_BULK_UPLOAD_ORG_SUCCESS;
    new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues,
        getOrgBulkUploadUrl(), requestFormData, testFolderPath, getHeaderWithAuthToken());

    http()
        .client(restTestClient)
        .receive()
        .response(HttpStatus.OK)
        .payload(new ClassPathResource(responseJson));

  }

  @Test(
      dataProvider = "orgBulkUploadFailure"
  )
  @CitrusParameters({"requestFormData", "responseJson", "testName"})
  @CitrusTest
  /**
   * Method to validate functional test cases for organisation bulk upload for various failure scenarios -
   * 1.upload csv file with invalid fields and expecting BAD request in response.
   * 2.upload csv file with empty file and expecting BAD request in response.
   * 3.call api without attaching csv file and excepting server error exception. //TODO: need to correct the code to throw CLIENT_ERROR
   */
  public void testOrgBulkUploadFailureCases(String requestFormData, String responseJson, String testName) {
    getTestCase().setName(testName);
    String testFolderPath;

    if ((TEST_DIR_BULK_UPLOAD_ORG_RESPONSE + "failureResponse.json")
        .equals(responseJson)) {
      testFolderPath = TEST_DIR_BULK_UPLOAD_ORG_FAILURE;
      new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues,
          getOrgBulkUploadUrl(), requestFormData, testFolderPath, getHeaderWithAuthToken());

      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    }else if((TEST_DIR_BULK_UPLOAD_ORG_RESPONSE + "emptyFileFailureResponse.json").equals(responseJson)){
      testFolderPath = TEST_DIR_BULK_UPLOAD_ORG_FAILURE;
      new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues,
          getOrgBulkUploadUrl(), requestFormData, testFolderPath, getHeaderWithAuthToken());

      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    }else if((TEST_DIR_BULK_UPLOAD_ORG_RESPONSE + "fileAbsentFailureResponse.json").equals(responseJson)){
      testFolderPath = TEST_DIR_BULK_UPLOAD_ORG_FAILURE;
      new HttpUtil().multipartPost(http().client(restTestClient), initGlobalValues,
          getOrgBulkUploadUrl(), requestFormData, testFolderPath, getHeaderWithAuthToken());

      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.INTERNAL_SERVER_ERROR)
          .payload(new ClassPathResource(responseJson));
    }
  }

  private String getOrgBulkUploadUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/org/upload"
        : "/api/org/v1/upload";
  }


}
