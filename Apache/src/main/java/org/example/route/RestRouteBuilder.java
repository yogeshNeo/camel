package org.example.route;

import org.example.processors.PersonProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.example.processors.PersonAggregationStrategy;
import org.apache.camel.model.rest.RestBindingMode;
import org.example.model.Person;

// @Component
public class RestRouteBuilder extends ExceptionRouteBuilder {

    @Value("${server.port}")
    String serverPort;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Autowired
    private PersonProcessor personProcessor;

    @Autowired
    private PersonAggregationStrategy personAggregationStrategy;

    @Override
    public void configure() throws Exception {
        super.configure();

        restConfiguration().contextPath(contextPath)
                .port(serverPort)
                .enableCORS(true)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "REST API")
                .apiProperty("api.version", "v1")
                .apiProperty("cors", "true") // cross-site
                .apiContextRouteId("doc-api")
         //       .component("Person")
                .bindingMode(RestBindingMode.auto)
                .dataFormatProperty("prettyPrint", "true");

        rest("/person").id("rest-person")
                // "/person" or "/person?firstName=First" or "/person?lastName=Last"
                .get("").id("rest-person-get").consumes("application/json").produces("application/json").to("direct:getAllPerson")
                // "/person/id"
                .get("/{id}").id("rest-person-get-id").consumes("application/json").produces("application/json").to("direct:getSinglePerson")
                .post("").id("rest-person-post").consumes("application/json").produces("application/json").type(Person.class).to("direct:postPerson");

        from("direct:getAllPerson")
                .id("direct-getAllPerson")
                .to("log:DEBUG?showBody=true&showHeaders=true")
                .process(personProcessor);

        from("direct:getSinglePerson")
                .id("direct-getSinglePerson")
                .to("log:DEBUG?showBody=true&showHeaders=true")
                .bean(personProcessor, "getPerson");

        from("direct:postPerson")
                .id("direct-postPerson")
                .to("log:DEBUG?showBody=true&showHeaders=true")
                .bean(personProcessor, "insertPerson");

        rest("/person/name")
                // "/person/name?firstName=First&lastName=Last"
                .get("").id("rest-person-name").consumes("application/json").produces("application/json").to("direct:getFirstAndLastNames");

        from("direct:getFirstAndLastNames")
                .multicast()
                .aggregationStrategy(personAggregationStrategy)
                .parallelProcessing()
                .streaming()
                .to("seda:getPeopleByFirstName", "seda:getPeopleByLastName");

        from("seda:getPeopleByFirstName")
                .bean(personProcessor, "getPeopleByFirstName");

        from("seda:getPeopleByLastName")
                .bean(personProcessor, "getPeopleByLastName");
    }
}