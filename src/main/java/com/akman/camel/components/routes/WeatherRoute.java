package com.akman.camel.components.routes;

import com.akman.camel.dto.WeatherDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.akman.camel.config.CamelConfiguration.RABBIT_URI;
import static org.apache.camel.LoggingLevel.ERROR;

@Component
@Slf4j
public class WeatherRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherRoute.class);

    @Override
    public void configure() {
        fromF(RABBIT_URI, "weather", "weather")
                .log(ERROR, "Before Enrichment: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .process(this::enrichWeatherDto)
                .log(ERROR, "After Enrichment: ${body}")
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .toF(RABBIT_URI, "weather-events", "weather-events")
                .to("file:///Users//akman//akman//docker//rabbit/?fileName=weather-events.txt&fileExist=Append")
        ;
    }

    private void enrichWeatherDto(Exchange exchange) {
        LOGGER.info("{}", exchange);
        WeatherDto dto = exchange.getMessage().getBody(WeatherDto.class);
        LOGGER.info("{}", dto);
        dto.setReceivedTime(new Date().toString());
        LOGGER.info("{}", dto);

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}
