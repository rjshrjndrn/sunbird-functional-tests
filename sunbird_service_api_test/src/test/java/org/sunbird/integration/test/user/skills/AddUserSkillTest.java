/**
 * 
 */
package org.sunbird.integration.test.user.skills;

import javax.ws.rs.core.MediaType;

import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;

/**
 * @author RKHema
 *
 */
public class AddUserSkillTest extends BaseCitrusTest {

	  public static final String TEST_NAME_ADD_USER_SKILL_FAILURE_WITHOUT_ACCESS_TOKEN =
	      "testAddUserSkillFailureWithoutAccessToken";
	  public static final String TEST_NAME_ADD_USER_SKILL_FAILURE_WITH_INVALID_USER_ID =
		      "testAddUserSkillFailureWithInvalidUserId";
	  public static final String TEST_NAME_ADD_USER_SKILL_FAILURE_WITHOUT_ENDORSED_USER_ID =
		      "testAddUserSkillFailureWithoutEndorsedUserId";
	  
	  
	  public static final String TEMPLATE_DIR = "templates/user/skill/add";

	  private String getAddUserSkillUrl() {

	    return getLmsApiUriPath("/api/user/v1/skill/add", "/v1/user/skill/add");
	  }

	  @DataProvider(name = "addUserSkillFailureDataProvider")
	  public Object[][] addUserSkillFailureDataProvider() {

	    return new Object[][] {
	      new Object[] {TEST_NAME_ADD_USER_SKILL_FAILURE_WITHOUT_ACCESS_TOKEN, false ,HttpStatus.UNAUTHORIZED},
	      new Object[] {TEST_NAME_ADD_USER_SKILL_FAILURE_WITH_INVALID_USER_ID, true ,HttpStatus.BAD_REQUEST},
	      new Object[] {TEST_NAME_ADD_USER_SKILL_FAILURE_WITHOUT_ENDORSED_USER_ID, true ,HttpStatus.BAD_REQUEST},	     
	    };
	  }

	  @Test(dataProvider = "addUserSkillFailureDataProvider")
	  @CitrusParameters({"testName", "isAccessToken","httpStatusCode"})
	  @CitrusTest
	  public void testAddUserSkillFailure(String testName, boolean isAccessToken,HttpStatus httpStatusCode) {

	    performPostTest(
	        testName,
	        TEMPLATE_DIR,
	        getAddUserSkillUrl(),
	        REQUEST_JSON,
	        httpStatusCode,
	        RESPONSE_JSON,
	        isAccessToken,
	        MediaType.APPLICATION_JSON);
	  }
	}

