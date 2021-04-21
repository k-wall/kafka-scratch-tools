package com.company;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaLotsOfTopics {
    private static final Logger log = LogManager.getLogger(KafkaLotsOfTopics.class);


    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.setProperty(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put("sasl.mechanism", "OAUTHBEARER");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");
        props.put("sasl.login.callback.handler.class", "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");

        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);

//        props.put("sasl.mechanism", "PLAIN");
//        props.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"srvc-acct-9a20b8b4-18e8-4e6c-a89e-12a118e4b86b\"   password=\"420d1e77-1f68-47d1-a4a8-1d38c8d5ada5\";");


        try (final AdminClient adminClient = AdminClient.create(props)) {
            try {

                final String ordersTopic = System.getenv("TOPIC") == null ? "testtopic" : System.getenv("TOPIC") ;
                int i = 0;
                while(true) {
                    NewTopic newTopic = new NewTopic(ordersTopic + i, 3, (short)1);

                    // Create topic, which is async call.
                    final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));
                    Map<String, KafkaFuture<Void>> values = createTopicsResult.values();
                    values.get(ordersTopic + i).get();
                    i++;

                    if (i % 500 == 0) {
                        log.info("Created {}}", i);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                log.warn("Failed", e);
                if (!(e.getCause() instanceof TopicExistsException))
                    throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}