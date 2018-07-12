package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateOrganisationTest extends BaseCitrusTest {

  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITHOUT_NAME =
      "testCreateSubOrgFailureWithoutName";
  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_PROVIDER_WITHOUT_EXTERNAL_ID =
      "testCreateSubOrgFailureWithProviderWithoutExternalId";
  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_LOCATION_CODE =
      "testCreateSubOrgFailureWithInvalidLocationCode";
  public static final String TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_CHANNEL =
      "testCreateSubOrgFailureWithInvalidChannel";

  public static final String TEMPLATE_DIR = "templates/organisation/create";

  @Autowired private TestGlobalProperty config;

  private String getCreateOrgUrl() {
    return config.getLmsUrl().contains("localhost") ? "/v1/org/create" : "/org/v1/create";
  }

  @DataProvider(name = "createFailureOrgDataProvider")
  public Object[][] createFailureOrgDataProvider() {

    return new Object[][] {
      new Object[] {REQUEST_JSON, RESPONSE_JSON, TEST_NAME_CREATE_SUB_ORG_FAILURE_WITHOUT_NAME},
      new Object[] {
        REQUEST_JSON,
        RESPONSE_JSON,
        TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_PROVIDER_WITHOUT_EXTERNAL_ID
      },
      new Object[] {
        REQUEST_JSON, RESPONSE_JSON, TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_LOCATION_CODE
      },
      new Object[] {
        REQUEST_JSON, RESPONSE_JSON, TEST_NAME_CREATE_SUB_ORG_FAILURE_WITH_INVALID_CHANNEL
      }
    };
  }

  @Test(dataProvider = "createFailureOrgDataProvider")
  @CitrusParameters({"requestJson", "responseJson", "testName"})
  @CitrusTest
  public void testCreateOrganisationFailure(
      String requestJson, String responseJson, String testName) {
    performPostTest(
        testName,
        TEMPLATE_DIR,
        getCreateOrgUrl(),
        requestJson,
        HttpStatus.BAD_REQUEST,
        responseJson,
        true,
        MediaType.APPLICATION_JSON);
  }
}
