package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GetUserByLoginIdTest extends BaseCitrusTest {
  public static final String TEMPLATE_DIR = "templates/user/getbyloginid";
  private static final String GET_USER_BY_ID_SERVER_URI = "/api/user/v1/profile/read";
  private static final String GET_USER_BY_ID_LOCAL_URI = "/v1/user/getuser";

  @DataProvider(name = "getUserByLoginIdFailure")
  public Object[][] getUserByLoginIdFailure() {
    return new Object[][] {
      new Object[] {"testGetUserByLoginIdFailureWithInvalidLoginId"},
      new Object[] {"testGetUserByLoginIdFailureWithEmptyLoginId"},
      new Object[] {"testGetUserByLoginIdFailureWithoutLoginId"}
    };
  }

  @Test(dataProvider = "getUserByLoginIdFailure")
  @CitrusParameters({"testName"})
  @CitrusTest
  public void testGetUserByLoginIdFailure(String testName) {
    performPostTest(
        testName,
        TEMPLATE_DIR,
        getLmsApiUriPath(GET_USER_BY_ID_SERVER_URI, GET_USER_BY_ID_LOCAL_URI),
        REQUEST_JSON,
        HttpStatus.BAD_REQUEST,
        RESPONSE_JSON,
        false,
        MediaType.APPLICATION_JSON);
  }
}
