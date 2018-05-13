package org.sunbird.common.util;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * This class will provide the cassandra Session object to do the
 * db operation.
 * @author Manzarul
 *
 */
public class CassandraConnectionUtil {

  private static Session session;	
	
  /**
   * This method will take cassandra ip,port and keyspace and try to make the
   * make the connection. once connection is established it will return Session.	
   * @param ip String
   * @param port String
   * @param keySpace String
   * @return Session
   */
	public static Session getCassandraSession(String ip, String port, String keySpace) {
		if (session == null) {
			Cluster cluster = Cluster.builder().addContactPoint(ip).withPort(Integer.parseInt(port)).build();
			session = cluster.connect(keySpace);
		}
		return session;
	}
 
}
