package org.sunbird.integration.test.common.malformed;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired private TestGlobalProperty initGlobalValues;
  public static final String TEMPLATE_DIR = "templates/common/malformed";

  @DataProvider(name = "createRequestDataProvider")
  public Object[][] createRequestDataProvider() {
    return new Object[][]{
        new Object[]{
            getCreateUserUrl(),
            "userCreateFailureWithoutContentType", null
        },
        new Object[]{
            getCreatePagerUrl(), "pageCreateFailureWithoutContentType", null
        },
        new Object[]{
            getCreateUserNotesUrl(),
            "notesCreateFailureWithoutContentType", null
        },
        new Object[]{
            getCreateOrgUrl(), "orgCreateFailureWithoutContentType", null
        },
        new Object[]{
            getCreateCourseBatchUrl(),
            "batchCreateFailureWithoutContentType", null
        },
        new Object[]{
            getCreateUserUrl(),
            "userCreateFailureWithoutContentType", Constant.CONTENT_TYPE_APPLICATION_JSON_LD
        },
        new Object[]{
            getCreatePagerUrl(), "pageCreateFailureWithoutContentType",
            Constant.CONTENT_TYPE_APPLICATION_JSON_LD
        },
        new Object[]{
            getCreateUserNotesUrl(),
            "notesCreateFailureWithoutContentType", Constant.CONTENT_TYPE_APPLICATION_JSON_LD
        },
        new Object[]{
            getCreateOrgUrl(), "orgCreateFailureWithoutContentType",
            Constant.CONTENT_TYPE_APPLICATION_JSON_LD
        },
        new Object[]{
            getCreateCourseBatchUrl(),
            "batchCreateFailureWithoutContentType", Constant.CONTENT_TYPE_APPLICATION_JSON_LD
        }

    };
  }

  @Test(dataProvider = "createRequestDataProvider")
  @CitrusParameters({"uriPath", "testName", "contentType"})
  @CitrusTest
  public void testRequestWithoutContentType(
      String url, String testName, String contentType) {
    performPostTest(
        testName,
        TEMPLATE_DIR,
        url,
        REQUEST_JSON,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON,
        true,
        contentType);
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
