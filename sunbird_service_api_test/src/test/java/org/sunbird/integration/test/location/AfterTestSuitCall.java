package org.sunbird.integration.test.location;

import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerAfterSuiteSupport;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.sunbird.common.util.CassandraConnectionUtil;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.CreateUserTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;


public class AfterTestSuitCall extends TestRunnerAfterSuiteSupport {

	@Autowired
	private TestGlobalProperty initGlobalValues;

	/**
	 * This method will execute at the end of test case.
	 */
	@Override
	public void afterSuite(TestRunner testRunner) {
		System.out.println("After test method called" + initGlobalValues);
		// delete from cassandra.
		BaseCitrusTest.toDeleteCassandraRecordsMap.forEach((k, v) -> {
			if (v != null)
				for (String value : v) {
					deleteDataFromCassandra(value, k);
					System.out.println("Deleted from Cassandra for Type and id ==" + k + " " + v);
				}
		});
		//delete from elastic search.
		BaseCitrusTest.toDeleteEsRecordsMap.forEach((k, v) -> {
			if (v != null)
				for (String value : v) {
					boolean response = deleteDataFromES(initGlobalValues.getEsHost(), "9200",
							initGlobalValues.getIndex(), k, value);
					System.out.println("Deleted response from ES for Type and id ==" + k + " " + v + " " + response);
				}
		});
	}

	/**
	 * This method will deleted test data from ES
	 * 
	 * @param host
	 *            String host of Elastic search
	 * @param port
	 *            String port of ES
	 * @param index
	 *            String index name
	 * @param type
	 *            String type name
	 */
	private boolean deleteDataFromES(String host, String port, String index, String type, String id) {
		StringBuilder builder = new StringBuilder("http://");
		builder.append(host);
		builder.append(":" + port + "/" + index + "/" + type + "/" + id);
		System.out.println("Complete url is ===" + builder.toString());
		return HttpUtil.doDeleteOperation(builder.toString());
	}

	/**
	 * This method will delete created data from cassandra.
	 * 
	 * @param id
	 *            String identifier of table.
	 * @param tableName
	 *            String name of table.
	 */
	private boolean deleteDataFromCassandra(String id, String tableName) {
		String query = "DELETE FROM " + tableName + " WHERE id=" + "'" + id + "'";
		//Session session = CassandraConnectionUtil.getCassandraSession(initGlobalValues.getCassandraiP(), initGlobalValues.getCassandraPort(), initGlobalValues.getKeySpace(), initGlobalValues.getCassandraUserName(), initGlobalValues.getCassandraPassword());
		Session session = CassandraConnectionUtil.getCassandraSession(initGlobalValues.getCassandraiP(), initGlobalValues.getCassandraPort(), initGlobalValues.getKeySpace(), "cassandra", "password");
		ResultSet result = session.execute(query);
		if (result.isExhausted()) {
			return true;
		}
		return false;
	}

}
