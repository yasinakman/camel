package com.akman.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;

@SpringBootApplication(exclude = {
        JmxAutoConfiguration.class
})
public class CamelApplication {

    /*    public static void main(String[] args) {
            SimpleRouteBuilder routeBuilder = new SimpleRouteBuilder();
            try (CamelContext ctx = new DefaultCamelContext()) {
                ctx.addRoutes(routeBuilder);
                ctx.start();
                Thread.sleep((5 * 60 * 1000));
                ctx.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    public static void main(String[] args) {
        SpringApplication.run(CamelApplication.class, args);
    }
}
