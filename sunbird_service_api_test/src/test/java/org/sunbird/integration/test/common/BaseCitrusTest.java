package org.sunbird.integration.test.common;

import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.user.EndpointConfig;

/**
 * Class to contain the common things for all citrus  tests.
 * @author arvind.
 */
public class BaseCitrusTest extends TestNGCitrusTestDesigner {

  public static Map<String,List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
  public static Map<String,List<String>> toDeleteCassandraRecordsMap = new HashMap<String, List<String>>();
  public static Map<String,List<String>> toDeleteEsRecordsMap = new HashMap<String, List<String>>();

  public Map<String, Object> getHeaderWithAuthToken(){
    Map<String, Object> header = new HashMap<>();
    header.put(Constant.X_AUTHENTICATED_USER_TOKEN, EndpointConfig.admin_token);
    return header;
  }

}
