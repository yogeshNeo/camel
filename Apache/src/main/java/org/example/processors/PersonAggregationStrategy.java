package org.example.processors;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.example.model.Person;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Component
public class PersonAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
        List<Person> firstResult = oldExchange.getIn().getBody(List.class);
        List<Person> secondResult = newExchange.getIn().getBody(List.class);
        oldExchange.getIn().setBody(Stream.concat(firstResult.stream(), secondResult.stream()).distinct().collect(Collectors.toList()));
        return oldExchange;
    }
}
