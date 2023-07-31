package org.example.route;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.example.exception.FileNotFoundException;
import org.example.util.FileUtil;
import org.springframework.stereotype.Component;

@Component
public class FileRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(FileNotFoundException.class)
                .log("onException")
                .handled(true)
                .bean(FileUtil.class, "fileFailed")
                .to("log:error");

        errorHandler(deadLetterChannel("log:error").maximumRedeliveries(1));

        from("{{source.path}}")
                .routeId("In-File-Transfer-route")
                .to(ExchangePattern.InOut, "{{source.path}}")
                .log("File transferred Started")
                .bean(FileUtil.class, "handleFile")
                .to("{{destination.path}}")
                .log("File transferred successfully")
                .end();

    }
}
