package org.sunbird.integration.test.page.section;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.integration.test.common.BaseCitrusTestRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReadPageSectionTest extends BaseCitrusTestRunner {

	public static final String TEST_NAME_READ_PAGE_SECTION_FAILURE_WITHOUT_ACCESS_TOKEN =
			"testReadPageSectionFailureWithoutAccessToken";
	public static final String TEST_NAME_READ_PAGE_SECTION_FAILURE_WITH_INVALID_PAGE_ID =
			"testReadPageSectionFailureWithInvalidPageId";

	public static final String TEST_NAME_READ_PAGE_SETTING_FAILURE_WITHOUT_ACCESS_TOKEN =
			"testReadPageSettingFailureWithoutAccessToken";
	

	public static final String TEMPLATE_DIR = "templates/page/read";

	private String getReadPageUrl() {
		return getLmsApiUriPath("/api/data/v1/page/section/list", "/v1/page/section/list");
	}

	@DataProvider(name = "readPageFailureDataProvider")
	public Object[][] readPageFailureDataProvider() {

		return new Object[][] {

			new Object[] {
					TEST_NAME_READ_PAGE_SECTION_FAILURE_WITHOUT_ACCESS_TOKEN, false, HttpStatus.UNAUTHORIZED,        
			},
			
		};
	}

	@Test(dataProvider = "readPageFailureDataProvider")
	@CitrusParameters({"testName", "isAuthRequired", "httpStatusCode"})
	@CitrusTest
	public void testReadPageFailure(
			String testName, boolean isAuthRequired, HttpStatus httpStatusCode) {
		getAuthToken(this, isAuthRequired);

		String url = "";

		if(testName.equalsIgnoreCase(TEST_NAME_READ_PAGE_SECTION_FAILURE_WITHOUT_ACCESS_TOKEN)) {
			url = getReadPageUrl();
		}
		/*if(testName.equalsIgnoreCase(TEST_NAME_READ_PAGE_SETTING_FAILURE_WITHOUT_ACCESS_TOKEN)) {
			url = getReadPageSettingUrl()+"/id";
		}

*/
		performGetTest(
				this,
				TEMPLATE_DIR,
				testName,
				url,
				isAuthRequired,
				httpStatusCode,
				RESPONSE_JSON);
	}
}
