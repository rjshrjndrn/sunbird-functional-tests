package org.sunbird.integration.test.user;

import com.consol.citrus.dsl.design.TestDesigner;
import com.consol.citrus.dsl.design.TestDesignerBeforeSuiteSupport;

/**
 * This class will execute before any test call.
 * @author Manzarul
 *
 */
public class BeforeTestSuitCall  extends TestDesignerBeforeSuiteSupport  {

	@Override
	/**
	 * We can write some data clean up code , which will clean 
	 * data from cassandra and elastic search 
	 */
	public void beforeSuite(TestDesigner designer) {
		System.out.println("Before test start called.....");
		
	}

	/**
	 * This method will take elastic search host and port and will
	 * collect all test data ids, from different types inside sunbird search index.
	 * @param host String  host of Elastic search
	 * @param port String  port of ES
	 * @param index String  index name
	 * @param type String  type name
	 */
	private void getESTestData (String host,String port,String index,String type) {
		
	}
	

}
