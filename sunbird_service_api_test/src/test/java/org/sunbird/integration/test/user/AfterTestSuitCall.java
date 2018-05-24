package org.sunbird.integration.test.user;

import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerAfterSuiteSupport;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.sunbird.common.util.CassandraConnectionUtil;
import org.sunbird.common.util.HttpUtil;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

/**
 * This class is for data cleanup. it will do data clean from cassandra and ES.
 *
 * @author Manzarul
 */
public class AfterTestSuitCall extends TestRunnerAfterSuiteSupport {

  @Autowired private TestGlobalProperty initGlobalValues;
  private static final String USER = "user";

  /** This method will execute at the end of test case. */
  @Override
  public void afterSuite(TestRunner arg0) {
    /*System.out.println("After test method called" + initGlobalValues);

    List<String> userIds = collectAllCreatedUserList();
    System.out.println("List of user ids that need to be deleted.." + userIds);
    boolean deleteStatus = deleteDataFromCassandra(userIds, USER);
    System.out.println("user delete status ==" + deleteStatus);

    for (String k : userIds) {
    	boolean deleteResponse = deleteDataFromES(initGlobalValues.getEsHost(), initGlobalValues.getEsPort(),
    			initGlobalValues.getIndex(), USER, k);
    	System.out.println("User delete response from ES ==" + deleteResponse);
    }*/
  }

  /**
   * This method will deleted test data from ES
   *
   * @param host String host of Elastic search
   * @param port String port of ES
   * @param index String index name
   * @param type String type name
   */
  private boolean deleteDataFromES(String host, String port, String index, String type, String id) {
    return HttpUtil.doDeleteOperation(createURL(host, port, index, type, id));
  }

  /**
   * This method will delete created data from cassandra.
   *
   * @param ids List<String> identifier of table.
   * @param tableName String name of table.
   */
  private boolean deleteDataFromCassandra(List<String> ids, String tableName) {
    String query = createDeleteQuery(tableName, ids);
    Session session =
        CassandraConnectionUtil.getCassandraSession(
            initGlobalValues.getCassandraiP(),
            initGlobalValues.getCassandraPort(),
            initGlobalValues.getKeySpace());
    ResultSet result = session.execute(query);
    if (result.isExhausted()) {
      return true;
    }
    return false;
  }

  /**
   * This method will collect all the test user ids based on user first name our assumption is while
   * user creation will create all user having first name as 'ft_first_Name_pw12401'
   *
   * @return List<String>
   */
  private List<String> collectAllCreatedUserList() {
    String query = "select id from user where firstname='ft_first_Name_pw12401' ALLOW FILTERING";
    Session session =
        CassandraConnectionUtil.getCassandraSession(
            initGlobalValues.getCassandraiP(),
            initGlobalValues.getCassandraPort(),
            initGlobalValues.getKeySpace());
    ResultSet result = session.execute(query);
    List<Row> rowList = result.all();
    List<String> userIds = new ArrayList<>();
    if (rowList != null)
      for (Row row : rowList) {
        userIds.add(row.getString(0));
      }
    return userIds;
  }

  /**
   * This method will take list of ids and table name to create the delete query.if ids is null or
   * empty then it will throw run time exception. if table name is null or empty then also it will
   * throw run time exception
   *
   * @param tableName String should not be null or empty
   * @param ids List<String> should have at least one element.
   * @return String complete delete query.
   */
  private String createDeleteQuery(String tableName, List<String> ids) {
    if (ids == null || ids.size() == 0) {
      throw new RuntimeException("Ids can't be null or empty.");
    }
    if (StringUtils.isBlank(tableName)) {
      throw new RuntimeException("Table name can't be null or empty.");
    }
    StringBuilder builder = new StringBuilder("DELETE FROM " + tableName + " where id in ( ");
    for (int i = 0; i < ids.size(); i++) {
      if (i < ids.size() - 1) {
        builder.append("'" + ids.get(i) + "',");
      } else {
        builder.append("'" + ids.get(i) + "' );");
      }
    }

    return builder.toString();
  }

  /**
   * This method will create url for ES get and deleted by Id.
   *
   * @param host String
   * @param port String
   * @param index String
   * @param type String
   * @param id String
   * @return String
   */
  private String createURL(String host, String port, String index, String type, String id) {
    StringBuilder builder = new StringBuilder("http://");
    builder.append(host);
    builder.append(":" + port + "/" + index + "/" + type + "/" + id);
    System.out.println("Complete url is ===" + builder.toString());
    return builder.toString();
  }

  /**
   * This method will take elastic search get by id response and convert it to Map<String,Object>.
   * if response is empty or null then it will return empty map.
   *
   * @param response String
   * @return Map<String, Object>
   */
  private Map<String, Object> generateMapFromESResponse(String response) {
    if (StringUtils.isEmpty(response)) {
      return new WeakHashMap<>();
    }
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> map = null;
    try {
      map = mapper.readValue(response, new TypeReference<HashMap<String, Object>>() {});
    } catch (JsonParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return map;
  }

  /**
   * This method will take the input map and based on type it will decide what are the tables used
   * to store that type of data. then it will create an out put map which contains table name as key
   * and values as list of ids , that need to be deleted.
   *
   * @param inputMap Map<String,Object>
   * @param type String
   * @return Map<String,List<String>>
   */
  @SuppressWarnings("unchecked")
  private Map<String, List<String>> createCassandraDataDeleteMap(
      Map<String, Object> inputMap, String type) {
    Map<String, List<String>> outPutMap = new HashMap<>();
    if (USER.equalsIgnoreCase(type)) {
      List<String> ids = new ArrayList<>();
      ids.add((String) inputMap.get("userId"));
      outPutMap.put(USER, ids);
      List<Map<String, Object>> listOfEdu = (List<Map<String, Object>>) inputMap.get("education");
      if (listOfEdu != null && listOfEdu.size() > 0) {
        outPutMap.put("user_education", getIds(listOfEdu));
      }
      List<Map<String, Object>> address = (List<Map<String, Object>>) inputMap.get("address");
      if (address != null && address.size() > 0) {
        outPutMap.put("address", getIds(address));
      }
    }
    return outPutMap;
  }

  private List<String> getIds(List<Map<String, Object>> inputMap) {
    List<String> ids = new ArrayList<>();
    for (Map<String, Object> map : inputMap) {
      ids.add((String) map.get("id"));
    }
    return ids;
  }
}
