package org.sunbird.integration.test.user;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class will do the initialization of all global variable.
 *
 * @author Manzarul
 */
@Configuration
public class EndpointConfig {

  @Bean
  public HttpClient restTestClient() {
    return CitrusEndpoints.http()
        .client()
        .requestUrl(System.getenv("sunbird_test_base_url"))
        .build();
  }

  @Bean
  public TestGlobalProperty initGlobalValues() {
    TestGlobalProperty property = new TestGlobalProperty();
    property.setApiKey(System.getenv("sunbird_api_key"));
    property.setCassandraiP(System.getenv("sunbird_cassandra_host"));
    property.setCassandraPort(System.getenv("sunbird_cassandra_port"));
    property.setCassandraUserName(System.getenv("sunbird_cassandra_username"));
    property.setKeySpace(System.getenv("sunbird_cassandra_keyspace"));
    property.setKeycloakAdminUser(System.getenv("sunbird_sso_username"));
    property.setKeycloakAdminPass(System.getenv("sunbird_sso_password"));
    property.setRelam(System.getenv("sunbird_sso_realm"));
    property.setClientId(System.getenv("sunbird_sso_client_id"));
    property.setEsHost(System.getenv("sunbird_es_host"));
    property.setEsPort(System.getenv("sunbird_es_port"));
    property.setIndexType(System.getenv("sunbird_es_index_type"));
    property.setIndex(System.getenv("sunbird_es_index"));
    // System.getenv("sunbird_default_channel")
    property.setSunbirdDefaultChannel("sunbird-dev");
    property.setLmsUrl(System.getenv("sunbird_test_base_url"));
    return property;
  }

  /**
   * a class to hold all the variable details.
   *
   * @author Manzarul
   */
  public class TestGlobalProperty {

    private String apiKey;
    private String keycloakAdminUser;
    private String keycloakAdminPass;
    private String relam;
    private String cassandraiP;
    private String cassandraPort;
    private String keySpace;
    private String cassandraUserName;
    private String clientId;
    private String esHost;
    private String esPort;
    private String index;
    private String indexType;
    private String sunbirdDefaultChannel;
    private String lmsUrl;
    private String cassandraPassword;

    public String getApiKey() {
      return apiKey;
    }

    public void setApiKey(String apiKey) {
      this.apiKey = apiKey;
    }

    public String getKeycloakAdminUser() {
      return keycloakAdminUser;
    }

    public void setKeycloakAdminUser(String keycloakAdminUser) {
      this.keycloakAdminUser = keycloakAdminUser;
    }

    public String getKeycloakAdminPass() {
      return keycloakAdminPass;
    }

    public void setKeycloakAdminPass(String keycloakAdminPass) {
      this.keycloakAdminPass = keycloakAdminPass;
    }

    public String getRelam() {
      return relam;
    }

    public void setRelam(String relam) {
      this.relam = relam;
    }

    public String getCassandraiP() {
      return cassandraiP;
    }

    public void setCassandraiP(String cassandraiP) {
      this.cassandraiP = cassandraiP;
    }

    public String getCassandraPort() {
      return cassandraPort;
    }

    public void setCassandraPort(String cassandraPort) {
      this.cassandraPort = cassandraPort;
    }

    public String getKeySpace() {
      return keySpace;
    }

    public void setKeySpace(String keySpace) {
      this.keySpace = keySpace;
    }

    public String getCassandraUserName() {
      return cassandraUserName;
    }

    public void setCassandraUserName(String cassandraUserName) {
      this.cassandraUserName = cassandraUserName;
    }

    public String getClientId() {
      return clientId;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public String getEsHost() {
      return esHost;
    }

    public void setEsHost(String esHost) {
      this.esHost = esHost;
    }

    public String getEsPort() {
      return esPort;
    }

    public void setEsPort(String esPort) {
      this.esPort = esPort;
    }

    public String getIndex() {
      return index;
    }

    public void setIndex(String index) {
      this.index = index;
    }

    public String getIndexType() {
      return indexType;
    }

    public void setIndexType(String indexType) {
      this.indexType = indexType;
    }

    public String getSunbirdDefaultChannel() {
      return sunbirdDefaultChannel;
    }

    public void setSunbirdDefaultChannel(String sunbirdDefaultChannel) {
      this.sunbirdDefaultChannel = sunbirdDefaultChannel;
    }

    public String getLmsUrl() {
      return lmsUrl;
    }

    public void setLmsUrl(String lmsUrl) {
      this.lmsUrl = lmsUrl;
    }

    public String getCassandraPassword() {
      return cassandraPassword;
    }

    public void setCassandraPassword(String cassandraPassword) {
      this.cassandraPassword = cassandraPassword;
    }

    @Override
    public String toString() {
      return "TestGlobalProperty [apiKey="
          + apiKey
          + ", keycloakAdminUser="
          + keycloakAdminUser
          + ", keycloakAdminPass="
          + keycloakAdminPass
          + ", relam="
          + relam
          + ", cassandraiP="
          + cassandraiP
          + ", cassandraPort="
          + cassandraPort
          + ", keySpace="
          + keySpace
          + ", cassandraUserName="
          + cassandraUserName
          + ", clientId="
          + clientId
          + "]";
    }
  }

  public static String val;
  public static String externalId = "";
  public static String provider = "";

  static {
    externalId = String.valueOf(System.currentTimeMillis());
    provider = String.valueOf(System.currentTimeMillis() + 10);
  }

  static {
    val = UUID.randomUUID().toString();
  }
}
