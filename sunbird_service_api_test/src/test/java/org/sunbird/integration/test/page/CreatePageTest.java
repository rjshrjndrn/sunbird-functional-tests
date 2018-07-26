package org.sunbird.integration.test.page;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.OrgUtil;
import org.sunbird.common.action.PageUtil;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreatePageTest extends BaseCitrusTestRunner {

	public static final String BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS = "testCreateRootOrgSuccess";

	private static final String PAGE_NAME =
			"FT_Page_Name-" + String.valueOf(System.currentTimeMillis());

	public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN =
			"testCreatePageFailureWithoutAccessToken";
	public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_NAME =
			"testCreatePageFailureWithoutName";
	public static final String TEST_NAME_CREATE_PAGE_FAILURE_WITH_EXISTING_NAME =
			"testCreatePageFailureWithExistingName";

	public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME =
			"testCreatePageSuccessWithName";
	public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME_AND_ORG_ID =
			"testCreatePageSuccessWithNameAndOrgId";
	public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_PORTAL_MAP =
			"testCreatePageSuccessWithPortalMap";
	public static final String TEST_NAME_CREATE_PAGE_SUCCESS_WITH_APP_MAP =
			"testCreatePageSuccessWithAppMap";

	public static final String TEMPLATE_DIR = "templates/page/create";
	public static final String ORG_CREATE_ORG_TEMPLATE_DIR = "templates/organisation/create";

	private String getCreatePageUrl() {

		return getLmsApiUriPath("/api/data/v1/page/create", "/v1/page/create");
	}

	@DataProvider(name = "createPageFailureDataProvider")
	public Object[][] createPageFailureDataProvider() {

		return new Object[][] {
			new Object[] {
					TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED
			},
			new Object[] {TEST_NAME_CREATE_PAGE_FAILURE_WITHOUT_NAME, true, HttpStatus.BAD_REQUEST},
			new Object[] {TEST_NAME_CREATE_PAGE_FAILURE_WITH_EXISTING_NAME, true, HttpStatus.BAD_REQUEST},
		};
	}

	@Test(dataProvider = "createPageFailureDataProvider")
	@CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
	@CitrusTest
	public void testCreatePageFailure(
			String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
		getAuthToken(this, isAuthRequired);

		if (testName.equalsIgnoreCase(TEST_NAME_CREATE_PAGE_FAILURE_WITH_EXISTING_NAME)) {
			variable("pageName", PAGE_NAME);
			beforeTestCreatePage(testName);
		}

		performPostTest(
				this,
				TEMPLATE_DIR,
				testName,
				getCreatePageUrl(),
				REQUEST_JSON,
				MediaType.APPLICATION_JSON,
				isAuthRequired,
				httpStatusCode,
				RESPONSE_JSON);
	}

	@DataProvider(name = "createPageSuccessDataProvider")
	public Object[][] createPageSuccessDataProvider() {

		return new Object[][] {
			new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME, true, HttpStatus.OK},
			new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME_AND_ORG_ID, true, HttpStatus.OK},
			new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_PORTAL_MAP, true, HttpStatus.OK},
			new Object[] {TEST_NAME_CREATE_PAGE_SUCCESS_WITH_APP_MAP, true, HttpStatus.OK},
		};
	}

	@Test(dataProvider = "createPageSuccessDataProvider")
	@CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
	@CitrusTest
	public void testCreatePageSuccess(
			String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {

		getAuthToken(this, true);

		beforeTestCreatePage(testName);


		performPostTest(
				this,
				TEMPLATE_DIR,
				testName,
				getCreatePageUrl(),
				REQUEST_JSON,
				MediaType.APPLICATION_JSON,
				isAuthRequired,
				httpStatusCode,
				RESPONSE_JSON);
	}

	private void beforeTestCreatePage(String testName) {

		if (testName.equalsIgnoreCase(TEST_NAME_CREATE_PAGE_SUCCESS_WITH_NAME_AND_ORG_ID)) {
			variable("rootChannel", OrgUtil.getRootChannel());
			variable("rootExternalId", OrgUtil.getRootExternalId());
			OrgUtil.createOrg(
					this,
					testContext,
					ORG_CREATE_ORG_TEMPLATE_DIR,
					BT_TEST_NAME_CREATE_ROOT_ORG_SUCCESS,
					HttpStatus.OK);
		}

		if(testName.equalsIgnoreCase(TEST_NAME_CREATE_PAGE_FAILURE_WITH_EXISTING_NAME)) {
			PageUtil.createPage(
					this,
					testContext,
					TEMPLATE_DIR,
					TEST_NAME_CREATE_PAGE_FAILURE_WITH_EXISTING_NAME,
					HttpStatus.OK);
		}
	}


}
