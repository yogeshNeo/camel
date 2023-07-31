package org.example.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TimerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("timer://openAPI?repeatCount=1&timerName=In-HTTP-Route")
                .routeId("Call-Open-API-Timer")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .to("{{open.api}}")
                .process(exchange -> log.info("HTTP GET response is: {}", exchange.getIn().getBody(String.class)));

    }

}
