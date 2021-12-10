package com.akman.camel.config;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;


@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class CamelConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelConfiguration.class);

    public static final String RABBIT_URI = "rabbitmq:amq.direct?queue=%s&routingKey=%s&autoDelete=false" +
            "&concurrentConsumers=1" +
            "&arg.queue.x-message-ttl=2400000"; /*ttl time to live on the queue (40 minutes)*/
    /*asyncConsumer=true*/

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        LOGGER.info("Creating connection factory with: guest@localhost:" + 5672);
        return factory;
    }
}
