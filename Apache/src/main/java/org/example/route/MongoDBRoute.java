package org.example.route;

import com.mongodb.client.model.Filters;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class MongoDBRoute extends RouteBuilder implements Serializable {

    private final CamelContext camelCtx;

    public MongoDBRoute(CamelContext camelCtx) {
        this.camelCtx = camelCtx;
    }

    @Override
    public void configure() throws Exception {

        from("jetty:http://localhost:8080/ticket?httpMethodRestrict=POST")
                .id("insert-data-route")
                .log("Called ticket insert API")
                .to("mongodb:mongoBean?database=travel&collection=tickets&operation=save")
                .setBody(simple("${body}"));

        from("jetty:http://localhost:8080/ticket?httpMethodRestrict=GET")
                .id("find-by-id-route")
                .log("Called ticket fetch by Id API")
                .setBody(header("id"))
                .convertBodyTo(ObjectId.class)
                .to("mongodb:mongoBean?database=travel&collection=tickets&operation=findById")
                .to("log:INFO")
                .setBody(simple("${body}"));

        from("jetty:http://localhost:8080/tickets/?httpMethodRestrict=GET")
                .id("direct-findAll")
                .log("Called direct-findAll API")
                .to("mongodb:mongoBean?database=travel&collection=tickets&operation=findAll")
                .process(exchange -> System.out.println("Find All:" + exchange.getIn().getBody(List.class)))
                .setBody(simple("${body}"));

        from("jetty:http://localhost:8080/tickets?httpMethodRestrict=PUT")
                .id("update-route")
                .log("Called Update API")
                .to("mongodb:mongoBean?database=travel&collection=tickets&operation=update")
                .setBody(simple("${body}"))
                .log("Query returned: '${body}'");

        //  Bson conditionField2 = Filters.eq("name", "NDRC");

        from("jetty:http://localhost:8080/ticket?httpMethodRestrict=DELETE")
                .id("delete-by-id-route")
                .log("Called ticket delete by Id API")
                .setExchangePattern(ExchangePattern.InOnly)
                //.setBody(header("id"))
                //.convertBodyTo(ObjectId.class)
                .to("mongodb:mongoBean?database=travel&collection=tickets&operation=remove")
                //  .setBody(simple("${body}"))
                .to("direct:remove");

        try {
            Exchange exchange = ExchangeBuilder.anExchange(camelCtx)
                    .build();

            ProducerTemplate template = camelCtx.createProducerTemplate();

            from("jetty:http://localhost:8080/tickets?httpMethodRestrict=PUT")
                    .setHeader(Exchange.HTTP_PATH, simple("http://localhost:8080/tickets"))
                    .to("mongodb:mongoBean?database=travel&collection=tickets&operation=update")

                    .to("direct:update");

            List<Bson> body = new ArrayList<>();
            Bson filterField = Filters.eq("name", "HRTC");
            body.add(filterField);
            BsonDocument updateObj = new BsonDocument().append("$set", new BsonDocument("name", new BsonString("Darwin")));
            body.add(updateObj);
            Object result = template.requestBodyAndHeader("direct:update", body, MongoDbConstants.MULTIUPDATE, true);
        } catch (Exception e) {
            System.out.println("Camel Execution Error :: " + e);
            throw new RuntimeException(e);
        }

      /*  Exchange exchange = ExchangeBuilder.anExchange(camelCtx)
                .build();

        ProducerTemplate template = exchange.getContext().createProducerTemplate();

        System.out.println("template ctxt :: " + template.getCamelContext());

        Bson conditionField = Filters.eq("name", "NDRC");
            Object result = template.requestBody("direct:remove", conditionField);
        System.out.println("result :: " + result);*/

    }
}
