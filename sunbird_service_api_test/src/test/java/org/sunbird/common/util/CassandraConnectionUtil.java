package org.sunbird.common.util;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

public class CassandraConnectionUtil {
	
  public static Session getCassandraSession (String ip,String port, String keySpace) {
	  Cluster cluster = Cluster.builder().addContactPoint(ip).withPort(Integer.parseInt(port)).build();
	  return cluster.connect(keySpace);
  }

  
  
  public static void main(String[] args) {
	  String tableName = "user";
	  String id = "vcurc633r8901";
	  String query = "DELETE FROM " + tableName + " WHERE id=" + "'" + id + "'";
	  
	  Session session = CassandraConnectionUtil.getCassandraSession("localhost", "9042", "sunbird");
	 ResultSet result = session.execute(query);
	 System.out.println(result);
}
}
