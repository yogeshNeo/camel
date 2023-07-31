package org.example.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.example.exception.ExceptionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExceptionRouteBuilder extends RouteBuilder {

    @Autowired
    private ExceptionProcessor exceptionProcessor;

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(true)
                .id("exception-handler")
                .log("Exception: '${exception.stacktrace}'")
                .process(exceptionProcessor)
                .marshal()
                .json(JsonLibrary.Jackson);
    }
}
