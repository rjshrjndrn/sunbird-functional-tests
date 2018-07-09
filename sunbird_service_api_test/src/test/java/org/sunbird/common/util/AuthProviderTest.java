package org.sunbird.common.util;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class is responsible for creating user auth token. if any class required token then it can
 * get it form here.
 *
 * @author Manzarul
 */
@Test(priority = 1)
public class AuthProviderTest extends TestNGCitrusTestDesigner {

  public static volatile String user_auth_token = null;

  @Autowired private HttpClient restTestClient;
  @Autowired private TestGlobalProperty initGlobalValues;
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test(groups = {"authToken"})
  @CitrusTest
  public void getAuthToken() {
    if (StringUtils.isEmpty(user_auth_token)) {
      http()
          .client(restTestClient)
          .send()
          .post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
          .contentType("application/x-www-form-urlencoded")
          .payload(
              "client_id="
                  + initGlobalValues.getClientId()
                  + "&username="
                  + initGlobalValues.getSunbirdTestUserName()
                  + "&password="
                  + initGlobalValues.getSunbirdTestUserPassword()
                  + "&grant_type=password");
      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.OK)
          .validationCallback(
              new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
                @Override
                public void validate(
                    Map response, Map<String, Object> headers, TestContext context) {
                  Assert.assertNotNull(response.get("access_token"));
                  user_auth_token = (String) response.get("access_token");
                  System.out.println(" AuthProviderTest User auth token found " + user_auth_token);
                }
              });
    }
  }
}
