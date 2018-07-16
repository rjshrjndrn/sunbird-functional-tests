pipeline {
    environment {
        sunbird_api_key = "${sunbird_api_keys}"
        sunbird_cassandra_host = "10.10.4.4"
        sunbird_cassandra_port = "9042"
        sunbird_cassandra_username = "cassandra"
        sunbird_cassandra_keyspace = "sunbird"
        sunbird_sso_username = "shailesh-admin"
        sunbird_sso_password = "${sunbird_sso_passwords}"
        sunbird_sso_realm = "sunbird"
        sunbird_sso_client_id = "admin-cli"
        sunbird_es_host = "10.10.3.7"
        sunbird_es_port = "9300"
        sunbird_es_index = "searchindex"
        sunbird_test_base_url = "https://dev.open-sunbird.org"
		sunbird_sso_url="https://dev.open-sunbird.org/auth"
		sunbird_username="ft_org_admin@org.com"
		sunbird_default_channel="ft_test"
    }

    agent { label "build-slave" }

    stages {
        stage("build") {
            steps {
                sh '''
                cd sunbird_service_api_test
                ls
                mvn clean verify
                '''
            }
        }
    }
}

