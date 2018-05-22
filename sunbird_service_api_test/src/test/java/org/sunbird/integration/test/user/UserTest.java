/*
 *
 */

package org.sunbird.integration.test.user;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder.HttpClientReceiveActionBuilder;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseCode;
import org.sunbird.common.util.ConstantKeys;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * This class will have all functional test cases regarding create/update user, get user details ,
 * generate user auth key.
 *
 * @author Manzarul
 */
@Test(priority = 1)
public class UserTest extends TestNGCitrusTestDesigner {

  private static String userId = null;
  private static String user_auth_token = null;
  private static String admin_token = null;
  public static Map<String, List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
  private static final String CREATE_USER_URI = "/api/user/v1/create";
  private static final String UPDATE_USER_URI = "/api/user/v1/update";
  private static volatile String USER_NAME = "userName";
  private static String externalId = String.valueOf(System.currentTimeMillis());
  private static String provider = String.valueOf(System.currentTimeMillis() + 10);

  /**
   * User can define the api request and response json structure. first index is request json
   * object, second is response json object and third is test case name.
   *
   * @return
   */
  @DataProvider(name = "createUserDataProvider")
  public Object[][] createUserDataProvider() {
    return new Object[][] {
      new Object[] {
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_first_name_mandatory.json",
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_first_name_mandatory_response.json",
        "firstNameMandatoryTest"
      },
      new Object[] {
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_name_mandatory.json",
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_name_mandatory_response.json",
        "UserNameMandatory"
      },
      new Object[] {
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_invalid_role_type.json",
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_invalid_role_type_response.json",
        "invalidRoleType"
      },
      new Object[] {
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_invalid_language_type.json",
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_invalid_language_type_response.json",
        "invalidLanguageType"
      },
      new Object[] {
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_invalid_dob_format.json",
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_invalid_dob_response.json",
        "invalidDobFormat"
      }
    };
  }

  @DataProvider(name = "createUserDynamicDataProvider")
  public Object[][] createUserDynamicJsonData() {
    return new Object[][] {
      new Object[] {createUserMap(), "usersuccessresponse.json", "createUser"},
      new Object[] {
        createUserWithDuplicateEmail(),
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_duplicate_email_response.json",
        "duplicateEmailTest"
      },
      new Object[] {
        createUserWithDuplicateUserName(),
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_username_exist_response.json",
        "duplicateUsernameTest"
      },
      new Object[] {
        createUserWithDuplicateExtIdAndProvider(),
        ConstantKeys.USER_TEMPLATE_LOCATION + "user_already_exist_response.json",
        "duplicateExtIdAndProviderTest"
      },
      new Object[] {
        createUserWithInvalidChannel(),
        ConstantKeys.USER_TEMPLATE_LOCATION + "invalidChannelResponse.json",
        "invalidChannelTest"
      }
    };
  }

  @DataProvider(name = "updateUserDynamicDataProvider")
  public Object[][] updateUserDynamicDataProvider() {
    return new Object[][] {
      new Object[] {updateUserMapWithId(), "update_user_success_response.json", "updateUserWithId"},
      new Object[] {
        updateUserMapWithExtIdAndProvider(),
        "update_user_success_response.json",
        "updateUserMapWithExtIdAndProvider"
      },
      new Object[] {
        updateUserMapWithRegOrgId(),
        ConstantKeys.UPDATE_USER_TEMPLATE_LOCATION + "user_update_bad_request_response.json",
        "invalidRequestDataRegOrGIdTest"
      },
      new Object[] {
        updateUserMapWithRootOrgId(),
        ConstantKeys.UPDATE_USER_TEMPLATE_LOCATION + "user_update_bad_request_response.json",
        "invalidRequestDataRootOrgIdTest"
      },
      new Object[] {
        updateUserMapWithChannel(),
        ConstantKeys.UPDATE_USER_TEMPLATE_LOCATION + "user_update_bad_request_response.json",
        "invalidRequestDataChannelTest"
      }
    };
  }

  @Autowired private HttpClient restTestClient;
  @Autowired private TestGlobalProperty initGlobalValues;
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test(dataProvider = "createUserDynamicDataProvider", priority = 1)
  @CitrusParameters({"requestJson", "responseJson", "testName"})
  @CitrusTest
  public void testCreateUser(String requestJson, String responseJson, String testName) {
    System.out.println("Create user " + testName + " Data: " + requestJson);
    getTestCase().setName(testName);
    http()
        .client(restTestClient)
        .send()
        .post(CREATE_USER_URI)
        .contentType(ConstantKeys.CONTENT_TYPE_APPLICATION_JSON)
        .header(ConstantKeys.AUTHORIZATION, ConstantKeys.BEARER + initGlobalValues.getApiKey())
        .payload(requestJson);
    if (!"usersuccessresponse.json".equals(responseJson)) {
      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    } else {
      HttpClientReceiveActionBuilder response = http().client(restTestClient).receive();
      handleUserCreationResponse(response);
    }
  }

  @Test(dataProvider = "createUserDataProvider", priority = 2)
  @CitrusParameters({"requestJson", "responseJson", "testName"})
  @CitrusTest
  public void testCreateUserFailure(String requestJson, String responseJson, String testName) {
    getTestCase().setName(testName);
    http()
        .client(restTestClient)
        .send()
        .post(CREATE_USER_URI)
        .contentType(ConstantKeys.CONTENT_TYPE_APPLICATION_JSON)
        .header(ConstantKeys.AUTHORIZATION, ConstantKeys.BEARER + initGlobalValues.getApiKey())
        .payload(new ClassPathResource(requestJson));
    http()
        .client(restTestClient)
        .receive()
        .response(HttpStatus.BAD_REQUEST)
        .payload(new ClassPathResource(responseJson));
  }

  /**
   * This method will handle response for create user.
   *
   * @param response HttpClientReceiveActionBuilder
   */
  private void handleUserCreationResponse(HttpClientReceiveActionBuilder response) {
    response
        .response(HttpStatus.OK)
        .validationCallback(
            new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
              @Override
              public void validate(
                  Response response, Map<String, Object> headers, TestContext context) {
                Assert.assertNotNull(response.getId());
                Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
                Assert.assertNotNull(response.getResult().get("response"));
                userId = (String) response.getResult().get("userId");
                Assert.assertNotNull(userId);
                List<String> list = deletedRecordsMap.get("user");
                if (list == null) {
                  list = new ArrayList<>();
                }
                list.add(userId);
                deletedRecordsMap.put("user", list);
              }
            });
  }

  @Test(priority = 3)
  @CitrusTest
  /**
   * Key cloak admin token generation is required , because on sunbird dev server after creating
   * user , user have to login first then only his/her account will be active. so we need to disable
   * that option for created user only. That option can be disable using keycloak admin auth token.
   * So this method will generate auth token and that token will be used in
   * **updateUserRequiredLoginActionTest** method.
   */
  public void getAdminAuthToken() {
    http()
        .client(restTestClient)
        .send()
        .post("/auth/realms/master/protocol/openid-connect/token")
        .contentType("application/x-www-form-urlencoded")
        .payload(
            "client_id=admin-cli&username="
                + initGlobalValues.getKeycloakAdminUser()
                + "&password="
                + initGlobalValues.getKeycloakAdminPass()
                + "&grant_type=password");
    http()
        .client(restTestClient)
        .receive()
        .response(HttpStatus.OK)
        .validationCallback(
            new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
              @Override
              public void validate(Map response, Map<String, Object> headers, TestContext context) {
                Assert.assertNotNull(response.get("access_token"));
                admin_token = (String) response.get("access_token");
                System.out.println("Admin auth token value ==" + admin_token);
              }
            });
  }

  @Test(priority = 4)
  @CitrusTest
  /**
   * This method will disable user required action change password under keyCloak. after disabling
   * that , we can generate newly created user auth token.
   */
  public void updateUserRequiredLoginActionTest() {
    http()
        .client(restTestClient)
        .send()
        .put("/auth/admin/realms/" + initGlobalValues.getRelam() + "/users/" + userId)
        .header(ConstantKeys.AUTHORIZATION, ConstantKeys.BEARER + admin_token)
        .contentType(ConstantKeys.CONTENT_TYPE_APPLICATION_JSON)
        .payload("{\"requiredActions\":[]}");
    http().client(restTestClient).receive().response(HttpStatus.NO_CONTENT);
  }

  @Test(priority = 5)
  @CitrusTest
  public void getAuthToken() {
    http()
        .client(restTestClient)
        .send()
        .post("/auth/realms/" + initGlobalValues.getRelam() + "/protocol/openid-connect/token")
        .contentType("application/x-www-form-urlencoded")
        .payload(
            "client_id="
                + initGlobalValues.getClientId()
                + "&username="
                + USER_NAME
                + "@"
                + initGlobalValues.getSunbirdDefaultChannel()
                + "&password=password&grant_type=password");
    http()
        .client(restTestClient)
        .receive()
        .response(HttpStatus.OK)
        .validationCallback(
            new JsonMappingValidationCallback<Map>(Map.class, objectMapper) {
              @Override
              public void validate(Map response, Map<String, Object> headers, TestContext context) {
                Assert.assertNotNull(response.get("access_token"));
                user_auth_token = (String) response.get("access_token");
                System.out.println("User auth token value ==" + user_auth_token);
              }
            });
  }

  @Test(dataProvider = "updateUserDynamicDataProvider", priority = 6)
  @CitrusParameters({"requestJson", "responseJson", "testName"})
  @CitrusTest
  public void testUpdateUser(String requestJson, String responseJson, String testName) {
    System.out.println("Update user " + testName + " Data: " + requestJson);
    getTestCase().setName(testName);
    http()
        .client(restTestClient)
        .send()
        .patch(UPDATE_USER_URI)
        .contentType(ConstantKeys.CONTENT_TYPE_APPLICATION_JSON)
        .header(ConstantKeys.AUTHORIZATION, ConstantKeys.BEARER + initGlobalValues.getApiKey())
        .payload(requestJson)
        .header(ConstantKeys.X_AUTHENTICATED_USER_TOKEN, user_auth_token);
    if (!"update_user_success_response.json".equals(responseJson)) {
      http()
          .client(restTestClient)
          .receive()
          .response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    } else {
      HttpClientReceiveActionBuilder response = http().client(restTestClient).receive();
      handleUserUpdateResponse(response);
    }
  }

  @Test(priority = 7)
  @CitrusTest
  public void getUserTest() {
    http()
        .client(restTestClient)
        .send()
        .get("/v1/user/read/" + userId + "?Fields=completeness,missingFields,topic")
        .accept(ConstantKeys.CONTENT_TYPE_APPLICATION_JSON)
        .header(ConstantKeys.AUTHORIZATION, ConstantKeys.BEARER + initGlobalValues.getApiKey())
        .contentType(ConstantKeys.CONTENT_TYPE_APPLICATION_JSON)
        .header(ConstantKeys.X_AUTHENTICATED_USER_TOKEN, user_auth_token);
    http()
        .client(restTestClient)
        .receive()
        .response(HttpStatus.OK)
        .validationCallback(
            new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
              @Override
              public void validate(
                  Response response, Map<String, Object> headers, TestContext context) {
                Assert.assertNotNull(response.getId());
                Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
              }
            });
  }

  /**
   * This method will handle response for update user.
   *
   * @param response HttpClientReceiveActionBuilder
   */
  private void handleUserUpdateResponse(HttpClientReceiveActionBuilder response) {
    response
        .response(HttpStatus.OK)
        .validationCallback(
            new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
              @Override
              public void validate(
                  Response response, Map<String, Object> headers, TestContext context) {
                Assert.assertNotNull(response.getId());
                Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
                Assert.assertNotNull(response.getResult().get("response"));
              }
            });
  }

  private String createUserMap() {
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("request", createUserInnerMap());
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String createUserWithDuplicateExtIdAndProvider() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put(
        "email", ConstantKeys.USER_NAME_PREFIX + UUID.randomUUID().toString() + "@gmail.com");
    innerMap.put("userName", ConstantKeys.USER_NAME_PREFIX + UUID.randomUUID().toString());
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String createUserWithInvalidChannel() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put(
        "email", ConstantKeys.USER_NAME_PREFIX + UUID.randomUUID().toString() + "@gmail.com");
    innerMap.put("userName", ConstantKeys.USER_NAME_PREFIX + UUID.randomUUID().toString());
    innerMap.put("externalId", externalId + 123);
    innerMap.put("provider", provider + 234);
    innerMap.put("channel", "invalidChannel123123");
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String createUserWithDuplicateUserName() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put(
        "email", ConstantKeys.USER_NAME_PREFIX + UUID.randomUUID().toString() + "@gmail.com");
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String createUserWithDuplicateEmail() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put("userName", ConstantKeys.USER_NAME_PREFIX + UUID.randomUUID().toString());
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Map<String, Object> createUserInnerMap() {
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put("firstName", "ft_first_Name_pw12401");
    innerMap.put("lastName", "ft_lastName");
    innerMap.put("password", "password");
    innerMap.put("externalId", externalId);
    innerMap.put("provider", provider);
    USER_NAME = ConstantKeys.USER_NAME_PREFIX + EndpointConfig.val;
    String email = ConstantKeys.USER_NAME_PREFIX + EndpointConfig.val + "@gmail.com";
    innerMap.put("userName", USER_NAME);
    innerMap.put("email", email);
    return innerMap;
  }

  private String updateUserMapWithId() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put("lastName", "ft_lastName_updated");
    innerMap.put("id", userId);
    innerMap.put("userId", userId);
    innerMap.remove("userName");
    requestMap.put("request", innerMap);

    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String updateUserMapWithRegOrgId() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put("lastName", "ft_lastName_updated");
    innerMap.put("id", userId);
    innerMap.put("userId", userId);
    innerMap.put("regOrgId", "regOrgId");
    requestMap.put("request", innerMap);

    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String updateUserMapWithRootOrgId() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put("lastName", "ft_lastName_updated");
    innerMap.put("id", userId);
    innerMap.put("userId", userId);
    innerMap.put("rootOrgId", "rootOrgId");
    requestMap.put("request", innerMap);

    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String updateUserMapWithChannel() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put("lastName", "ft_lastName_updated");
    innerMap.put("id", userId);
    innerMap.put("userId", userId);
    innerMap.put("channel", "channel");
    requestMap.put("request", innerMap);

    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String updateUserMapWithExtIdAndProvider() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = createUserInnerMap();
    innerMap.put("lastName", "ft_lastName_updated_without_userid");
    innerMap.remove("userName");
    innerMap.put("externalId", externalId);
    innerMap.put("provider", provider);
    requestMap.put("request", innerMap);

    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
