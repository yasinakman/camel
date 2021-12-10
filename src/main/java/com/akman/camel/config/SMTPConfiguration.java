package com.akman.camel.config;

import com.swisscom.hyperion.utilities.smtp.pool.SmtpConnectionPool;
import com.swisscom.hyperion.utilities.smtp.transport.factory.SmtpConnectionFactory;
import com.swisscom.hyperion.utilities.smtp.transport.factory.SmtpConnectionFactoryBuilder;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import java.util.Properties;

@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class SMTPConfiguration {

    @Bean
    public SmtpConnectionFactory smtpConnectionFactory() {
        return SmtpConnectionFactoryBuilder.newSmtpBuilder()
                .session(smtpSession())
                .build();
    }

    @Bean
    public SmtpConnectionPool smtpConnectionPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        //genericObjectPoolConfig.setJmxEnabled(true);
        return new SmtpConnectionPool(smtpConnectionFactory(), genericObjectPoolConfig);
    }

    @Bean
    public Properties smtpProperties() {
        Properties properties = new Properties();

        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        return properties;
    }

    @Bean
    public Session smtpSession() {
        return Session.getDefaultInstance(smtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("spoty.yasin.1@gmail.com", "Yasin.044");
            }
        });
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(13);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
