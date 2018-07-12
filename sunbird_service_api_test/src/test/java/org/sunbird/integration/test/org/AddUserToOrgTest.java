package org.sunbird.integration.test.org;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddUserToOrgTest extends BaseCitrusTest {

	public static final String TEST_ADD_USER_TO_ORG_FAILURE_WITH_EMPTY_ROLE_ARRAY =
			"testAddUserToOrgFailureWithEmptyRoleArray";
	
	public static final String TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_USER_ID =
			"testAddUserToOrgFailureWithInvalidUserId";
	public static final String TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_ORG_ID =
			"testAddUserToOrgFailureWithInvalidOrgId";
	
	public static final String TEMPLATE_DIR = "templates/organisation/user/add";

	@Autowired private TestGlobalProperty config;

	private String getCreateOrgUrl() {
		return getLmsApiUriPath("/org/v1/member/add","v1/org/member/add");
	}

	@DataProvider(name = "memeberAddToOrgFailureDataProvider")
	public Object[][] memeberAddToOrgFailureDataProvider() {

		return new Object[][] {
			new Object[] {REQUEST_JSON, RESPONSE_JSON, TEST_ADD_USER_TO_ORG_FAILURE_WITH_EMPTY_ROLE_ARRAY},	
			new Object[] {REQUEST_JSON, RESPONSE_JSON, TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_USER_ID},	
			new Object[] {REQUEST_JSON, RESPONSE_JSON, TEST_NAME_ADD_USER_TO_ORG_FAILURE_WITH_INVALID_ORG_ID},	
		};
	}

	@Test(dataProvider = "memeberAddToOrgFailureDataProvider")
	@CitrusParameters({"requestJson", "responseJson", "testName"})
	@CitrusTest
	public void testMemeberAddToOrgFailure(
			String requestJson, String responseJson, String testName) {
		
		String contentType = "application/json";
		boolean isAuthRequired = true;
		
		performPostTest(
				testName,
				TEMPLATE_DIR,
				getCreateOrgUrl(),
				requestJson,
				HttpStatus.BAD_REQUEST,
				responseJson,isAuthRequired,contentType);
	}
}
