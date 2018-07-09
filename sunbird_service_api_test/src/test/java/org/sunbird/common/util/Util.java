package org.sunbird.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

/** @author Manzarul */
public class Util {

  @Autowired private TestGlobalProperty initGlobalValues;

  /**
   * This method will check if url contains localhost then provide downstream uri else upstream uri.
   *
   * @param upstreamUri uri for server
   * @param downStreamUri uri for local
   * @return uri
   */
  public String getUriBasedOnHost(String upstreamUri, String downStreamUri) {
    String uri = initGlobalValues.getLmsUrl().contains("localhost") ? downStreamUri : upstreamUri;
    return uri;
  }
}
