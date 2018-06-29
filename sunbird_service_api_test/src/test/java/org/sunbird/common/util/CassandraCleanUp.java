package org.sunbird.common.util;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.sunbird.integration.test.user.EndpointConfig;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

/**
 * Class to perform the clean up things from cassandra database.
 * @author arvind.
 */
public class CassandraCleanUp {

  private CassandraCleanUp(){

  }

  private static CassandraCleanUp cassandraCleanUp = null;

  public static CassandraCleanUp getInstance(){
    if(null == cassandraCleanUp){
      cassandraCleanUp = new CassandraCleanUp();
    }
    return cassandraCleanUp;
  }

 // @Autowired
  private TestGlobalProperty initGlobalValues = new EndpointConfig().initGlobalValues();

  /**
   * Method to delete the entries from cassandra.
   * @param map contains Table name as key and list of ids to be delete from
   * cassandra as list of String.
   */
  public void deleteFromCassandra(Map<String, List<String>> map){
    map.forEach((k, v) -> {
      if (v != null)
        for (String value : v) {
        
          deleteDataFromCassandra(value, k);
         
        }
    });
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
   
    Session session = CassandraConnectionUtil.getCassandraSession(initGlobalValues.getCassandraiP(), initGlobalValues.getCassandraPort(), initGlobalValues.getKeySpace(), initGlobalValues.getCassandraUserName(), initGlobalValues.getCassandraPassword());
    ResultSet result = session.execute(query);
    if (result.isExhausted()) {
      return true;
    }
    return false;
  }

}
