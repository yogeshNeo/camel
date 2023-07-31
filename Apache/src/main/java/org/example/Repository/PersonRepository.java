package org.example.Repository;

import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.spi.RestApiConsumerFactory;
import org.apache.camel.spi.RestApiProcessorFactory;
import org.example.model.Person;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public interface PersonRepository {

    public List<Person> findByFirstName(String firstName);
    public List<Person> findByLastName(String lastName);
    public List<Person> findAll();
}
