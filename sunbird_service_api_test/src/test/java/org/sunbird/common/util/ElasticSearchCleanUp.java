package org.sunbird.common.util;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

/**
 * Class to perform the clean up from elastic search.
 *
 * @author arvind.
 */
public class ElasticSearchCleanUp {

  @Autowired private TestGlobalProperty initGlobalValues;

  private static ElasticSearchCleanUp elasticSearchcleanUp;

  private ElasticSearchCleanUp() {}

  public static ElasticSearchCleanUp getInstance() {
    if (null == elasticSearchcleanUp) {
      elasticSearchcleanUp = new ElasticSearchCleanUp();
    }
    return elasticSearchcleanUp;
  }

  public void deleteFromElasticSearch(Map<String, List<String>> map) {
    map.forEach(
        (k, v) -> {
          if (v != null)
            for (String value : v) {
              boolean response =
                  deleteDataFromES(
                      initGlobalValues.getEsHost(),
                      Constant.ES_REST_API_PORT,
                      initGlobalValues.getIndex(),
                      k,
                      value);
            }
        });
  }

  private boolean deleteDataFromES(String host, String port, String index, String type, String id) {
    return HttpUtil.doDeleteOperation(createURL(host, port, index, type, id));
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
  private static String createURL(String host, String port, String index, String type, String id) {
    StringBuilder builder = new StringBuilder("http://");
    builder.append(host);
    builder.append(":" + port + "/" + index + "/" + type + "/" + id);
    return builder.toString();
  }
}
