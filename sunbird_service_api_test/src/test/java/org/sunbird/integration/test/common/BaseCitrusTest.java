package org.sunbird.integration.test.common;

import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arvind on 22/5/18.
 */
public class BaseCitrusTest extends TestNGCitrusTestDesigner {

  public static Map<String,List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
  public static Map<String,List<String>> toDeleteCassandraRecordsMap = new HashMap<String, List<String>>();
  public static Map<String,List<String>> toDeleteEsRecordsMap = new HashMap<String, List<String>>();

}
