package org.sunbird.integration.test.user.skills;

import javax.ws.rs.core.MediaType;

import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;

public class ReadUserSkillTest extends BaseCitrusTest {

	  public static final String TEST_NAME_READ_USER_SKILL_FAILURE_WITHOUT_ACCESS_TOKEN =
	      "testReadUserSkillFailureWithoutAccessToken";
	  public static final String TEST_NAME_READ_USER_SKILL_FAILURE_WITH_INVALID_ENDORSED_USER_ID =
		      "testReadUserSkillFailureWithInvalidEndorsedUserId";
	  public static final String TEST_NAME_READ_USER_SKILL_FAILURE_WITHOUT_ENDORSED_USER_ID =
		      "testReadUserSkillFailureWithoutEndorsedUserId";
	  
	  public static final String TEMPLATE_DIR = "templates/user/skill/read";

	  private String getReadUserSkillUrl() {

	    return getLmsApiUriPath("/api/user/v1/skill/read", "/v1/user/skill/read");
	  }

	  @DataProvider(name = "readUserSkillFailureDataProvider")
	  public Object[][] readUserSkillFailureDataProvider() {

	    return new Object[][] {
	      new Object[] {TEST_NAME_READ_USER_SKILL_FAILURE_WITHOUT_ACCESS_TOKEN, false ,HttpStatus.UNAUTHORIZED},
	      new Object[] {TEST_NAME_READ_USER_SKILL_FAILURE_WITH_INVALID_ENDORSED_USER_ID, true ,HttpStatus.BAD_REQUEST},
	      new Object[] {TEST_NAME_READ_USER_SKILL_FAILURE_WITHOUT_ENDORSED_USER_ID, true ,HttpStatus.BAD_REQUEST},
	      
	    };
	  }

	  @Test(dataProvider = "readUserSkillFailureDataProvider")
	  @CitrusParameters({"testName", "isAccessToken","httpStatusCode"})
	  @CitrusTest
	  public void testReadUserSkillFailure(String testName, boolean isAuthRequired,HttpStatus httpStatusCode) {

	    performPostTest(
	        testName,
	        TEMPLATE_DIR,
	        getReadUserSkillUrl(),
	        REQUEST_JSON,
	        httpStatusCode,
	        RESPONSE_JSON,
	        isAuthRequired,
	        MediaType.APPLICATION_JSON);
	  }
	}

