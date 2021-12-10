package com.akman.camel.components.routes;

import com.akman.camel.dto.MailDto;
import com.swisscom.hyperion.utilities.smtp.pool.SmtpConnectionPool;
import com.swisscom.hyperion.utilities.smtp.transport.connection.ClosableSmtpConnection;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.SerializationUtils;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.net.ConnectException;
import java.time.LocalDateTime;

import static org.apache.camel.LoggingLevel.ERROR;

@Component
@RequiredArgsConstructor
public class SimpleRoute extends RouteBuilder {

    private static final String HAS_EXCEPTION = "hasException";
    private final SmtpConnectionPool smtpConnectionPool;
    private RetryTemplate retrySmtpConnection = RetryTemplate.builder().maxAttempts(3).fixedBackoff(1000)
            .retryOn(Exception.class)
            /*.retryOn(JpaSystemException.class).retryOn(ConstraintViolationException.class)*/.build();

    @Override
    public void configure() {
        Processor emailSubject = exchange -> {
            try {
                retrySmtpConnection.execute(retryContext -> execution(exchange, retryContext));
                log.info("SUCCESSFUL e-mail sending with ConnectionPooling - {}", LocalDateTime.now());
            } catch (Exception e) {
                log.error("FAILED e-mail sending despite all attempts - {}, exception: {}", LocalDateTime.now(), e);
                exchange.getIn().setHeader(HAS_EXCEPTION, true);
            }
        };

        from("direct:simple")//.threads(5, 5).maxQueueSize(1000)
                .routeId(this.getClass().getSimpleName())
                .doTry()/*
                .setHeader("subject", simple("JavaInUse Invitation111"))
                .setHeader("to", simple("cyberjey17@gustr.com"))*/
                //.to("file:///Users//akman//akman//docker//rabbit/?fileName=mail-events.txt&fileExist=Append")
                //.log(ERROR, "After send Mail: ${body}")
                //.to(uri)
                .process(emailSubject)
                .process(exchange -> log.info("FINISH"))
                .endDoTry()
                .end();
    }/*

    @Recover
    public String recover (NullPointerException e){
        return "FAIL";
    }*/

    private Object execution(Exchange exchange, RetryContext retryContext) throws Exception {
        log.info("execute: - {}", LocalDateTime.now());
        if (retryContext.getRetryCount() > 0) {
            log.info("Retry snedingMessage via SmtpConnectionPool. Retry count is {}.",
                    retryContext.getRetryCount());
        }
        /**SMTP Connection Pool which uses JavaMail and Apache Common Pool*/
        ClosableSmtpConnection transport = smtpConnectionPool.borrowObject();
        try {/*
            String test = null;
            boolean empty = test.isEmpty();
*/
            MimeMessage mimeMessage = new MimeMessage(transport.getSession());
            String from = "spoty.yasin.1@gmail.com";
            String to = "yasintakman00@gustr.com";
            String subject = "emailSubject";
            String msgCont = ((MailDto) SerializationUtils.deserialize((byte[]) exchange.getIn().getBody())).getMessage();
            String messageContent = "Ich heiße Yasin Grün. Merhaba nasılsın iyisin inşallah. ";
            String messageType = "text/html; charset=utf-8";
            String utf8 = "UTF-8";
            String dispositionNotificationTo = "Disposition-Notification-To";

            mimeMessage.setFrom(new InternetAddress(from));
            //mimeMessage.addRecipients(RecipientType.TO, to);
            mimeMessage.setHeader("From", from);
            mimeMessage.setHeader(dispositionNotificationTo, from);
            mimeMessage.setRecipients(RecipientType.TO, to);
            mimeMessage.setSubject(subject, utf8);
            mimeMessage.setContent(mimeMessage, messageType);
            //mimeMessage.setSentDate(new Date());
            mimeMessage.setText(messageContent);
            mimeMessage.setSender(new InternetAddress(from));
            mimeMessage.setReplyTo(new InternetAddress[]{new InternetAddress(from)});
            transport.sendMessage(mimeMessage);
            return null;
        } catch (Exception e) {
            log.info("Transport Connection Check: - {}", LocalDateTime.now());
            if (!transport.isConnected()) {
                log.info("Reconnecting Transport: - {}", LocalDateTime.now());
                transport.getDelegate().connect();
                log.info("Transport Reconnected: - {}", LocalDateTime.now());
                throw new ConnectException();
            }

            log.error("FAILED to send e-mail via SmtpConnection - {}, exception: {}", LocalDateTime.now(), e);
            throw e;
        } finally {
            transport.close();
        }
    }

    /*        Properties mailServerProperties = new Properties();
        mailServerProperties.put("mail.smtp.host", "smtp.gmail.com");
        mailServerProperties.put("mail.smtp.port", 587);
        mailServerProperties.put("mail.smtp.auth", true);
        mailServerProperties.put("mail.smtp.starttls.enable", true);
        Session session = Session.getDefaultInstance(mailServerProperties, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication("spoty.yasin.1@gmail.com", "Yasin.044");
            }
        });

        SmtpConnectionFactory smtpConnectionFactory = SmtpConnectionFactoryBuilder.newSmtpBuilder()
                .session(session).build();
        SmtpConnectionPool smtpConnectionPool = new SmtpConnectionPool(smtpConnectionFactory);*/

    //from("file:C:/Users/akman/akman/docker/inputFolder?noop=true")
    /*todo yasin*///String uri = "smtps://smtp.gmail.com:465?username=spoty.yasin.1@gmail.com&password=Yasin.044";

}
