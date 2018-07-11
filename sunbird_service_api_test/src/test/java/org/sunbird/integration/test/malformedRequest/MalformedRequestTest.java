package org.sunbird.integration.test.malformedRequest;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.sunbird.integration.test.user.UserTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * This class will have all functional test cases regarding testing malformed request input for all
 * common APIs
 *
 * @author Karthik
 */
public class MalformedRequestTest extends BaseCitrusTest {

  @Autowired private HttpClient restTestClient;
  @Autowired private TestGlobalProperty initGlobalValues;
  private static String admin_token = null;
  private static final String CREATE_ORG_SERVER_URI = "/api/org/v1/create";
  private static final String CREATE_ORG_LOCAL_URI = "/v1/org/create";
  private static final String CREATE_COURSE_BATCH_SERVER_URI = "/api/course/v1/batch/create";
  private static final String CREATE_COURSE_BATCH_LOCAL_URI = "/v1/course/batch/create";
  private static final String CREATE_PAGE_SERVER_URI = "/api/data/v1/page/create";
  private static final String CREATE_PAGE_LOCAL_URI = "/v1/page/create";
  private static final String CREATE_USER_NOTES_SERVER_URI = "/api/notes/v1/create";
  private static final String CREATE_USER_NOTES_LOCAL_URI = "/v1/note/create";
  public static final String TEMPLATE_DIR = "templates/common/malformed";

  @DataProvider(name = "createRequestDataProvider")
  public Object[][] createRequestDataProvider() {
    return new Object[][] {
      new Object[] {
        UserTest.CREATE_USER_SERVER_URI,
        UserTest.CREATE_USER_LOCAL_URI,
        "userCreateFailureWithoutContentType"
      },
      new Object[] {
        CREATE_PAGE_SERVER_URI, CREATE_PAGE_LOCAL_URI, "pageCreateFailureWithoutContentType"
      },
      new Object[] {
        CREATE_USER_NOTES_SERVER_URI,
        CREATE_USER_NOTES_LOCAL_URI,
        "notesCreateFailureWithoutContentType"
      },
      new Object[] {
        CREATE_ORG_SERVER_URI, CREATE_ORG_LOCAL_URI, "orgCreateFailureWithoutContentType"
      },
      new Object[] {
        CREATE_COURSE_BATCH_SERVER_URI,
        CREATE_COURSE_BATCH_LOCAL_URI,
        "batchCreateFailureWithoutContentType"
      }
    };
  }

  @Test(dataProvider = "createRequestDataProvider")
  @CitrusParameters({"apiGatewayUriPath", "localUriPath", "testName"})
  @CitrusTest
  public void testRequestWithoutContentType(
      String apiGatewayUriPath, String localUriPath, String testName) {
    performPostTest(
        testName,
        TEMPLATE_DIR,
        getLmsApiUriPath(apiGatewayUriPath, localUriPath),
        REQUEST_JSON,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON,
        true,
        null);
  }

  /**
   * Test for create request with invalid(example json-ld) content-type header.
   *
   * @param requestJson - request input json
   * @param responseJson - response output json
   * @param testName - name of the name
   * @param url - url of API
   */
  // @Test(dataProvider = "createRequestDataProvider", dependsOnMethods = { "getAdminAuthToken" })
  // @CitrusParameters({ "requestJson", "responseJson", "testName", "url" })
  // @CitrusTest
  public void testRequestWithInvalidContentType(
      String requestJson, String responseJson, String testName, String url) {
    getTestCase().setName(testName);
    http()
        .client(restTestClient)
        .send()
        .post(url)
        .contentType(Constant.CONTENT_TYPE_APPLICATION_JSON_LD)
        .header(Constant.AUTHORIZATION, Constant.BEARER + initGlobalValues.getApiKey())
        .header(Constant.X_AUTHENTICATED_USER_TOKEN, admin_token)
        .payload(requestJson);

    http()
        .client(restTestClient)
        .receive()
        .response(HttpStatus.BAD_REQUEST)
        .payload(new ClassPathResource(responseJson));
  }
}
