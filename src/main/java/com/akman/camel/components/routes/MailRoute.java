package com.akman.camel.components.routes;

import com.akman.camel.dto.MailDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Date;

import static com.akman.camel.config.CamelConfiguration.RABBIT_URI;
import static org.apache.camel.LoggingLevel.ERROR;

@Component
@Slf4j
public class MailRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailRoute.class);

    @Override
    public void configure() {
        fromF(RABBIT_URI, "mail", "mail")//.threads(5, 5).maxQueueSize(1000)
                .routeId(this.getClass().getSimpleName())
                .log(ERROR, "Before Enrichment: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, MailDto.class)
                .process(this::enrichMailDto)
                .log(ERROR, "After Enrichment: ${body}")/*
                .marshal().json(JsonLibrary.Jackson, MailDto.class)*/
                .to("direct:simple")
                /*.toF(RABBIT_URI, "mail-events", "mail-events")*/
                //.to("file:///Users//akman//akman//docker//rabbit/?fileName=test.txt&fileExist=Append")
                .end();
    }

    private void enrichMailDto(Exchange exchange) {
        LOGGER.info("{}", exchange);
        MailDto dto = exchange.getMessage().getBody(MailDto.class);
        LOGGER.info("{}", dto);
        dto.setReceivedTime(new Date().toString());
        LOGGER.info("{}", dto);
/*
        Message message = new DefaultMessage(exchange);*/
        log.info("serialize start");
        /*message.setBody(SerializationUtils.serialize(dto), byte[].class);
        exchange.setMessage(message);*/
        exchange.getIn().setBody(SerializationUtils.serialize(dto/*.getMessage()*/), byte[].class);
        exchange.getIn().setHeaders(exchange.getIn().getHeaders());
        log.info("serialize end");
    }
}
