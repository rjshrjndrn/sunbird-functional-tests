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
  public static final String TEMPLATE_DIR = "templates/common/malformed";

  @DataProvider(name = "createRequestDataProvider")
  public Object[][] createRequestDataProvider() {
    return new Object[][] {
      new Object[] {
        getCreateUserUrl(),
        "userCreateFailureWithoutContentType"
      },
      new Object[] {
        getCreatePagerUrl(), "pageCreateFailureWithoutContentType"
      },
      new Object[] {
        getCreateUserNotesUrl(),
        "notesCreateFailureWithoutContentType"
      },
      new Object[] {
        getCreateOrgUrl(), "orgCreateFailureWithoutContentType"
      },
      new Object[] {
        getCreateCourseBatchUrl(),
        "batchCreateFailureWithoutContentType"
      }
    };
  }

  @Test(dataProvider = "createRequestDataProvider")
  @CitrusParameters({"uriPath", "testName"})
  @CitrusTest
  public void testRequestWithoutContentType(
      String url, String testName) {
    performPostTest(
        testName,
        TEMPLATE_DIR,
        url,
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

  private String getCreateUserUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/user/create"
        : "/api/user/v1/create";
  }

  private String getCreateOrgUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/org/create"
        : "/api/org/v1/create";
  }

  private String getCreatePagerUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/page/create"
        : "/api/data/v1/page/create";
  }

  private String getCreateCourseBatchUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/course/batch/create"
        : "/api/course/v1/batch/create";
  }

  private String getCreateUserNotesUrl() {
    return initGlobalValues.getLmsUrl().contains("localhost")
        ? "/v1/note/create"
        : "/api/notes/v1/create";
  }


}
