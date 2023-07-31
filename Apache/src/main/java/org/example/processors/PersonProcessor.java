package org.example.processors;

import org.apache.camel.Processor;
import org.example.Repository.PersonRepository;
import org.example.model.Person;
import org.example.util.CreateResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.camel.Exchange;
import org.example.model.Response;
import org.apache.camel.Header;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

@Component
public class PersonProcessor implements Processor {

    //@Autowired
    private PersonRepository personRepository ;

    @Override
    public void process(Exchange exchange) throws Exception {

        String firstName = (String) exchange.getIn().getHeader("firstName");
        String lastName = (String) exchange.getIn().getHeader("lastName");

        List<Person> people = null;

        if (StringUtils.isNotBlank(firstName)) {
            people = personRepository.findByFirstName(firstName);
        } else if (StringUtils.isNotBlank(lastName)) {
            people = personRepository.findByLastName(lastName);
        } else {
            people = personRepository.findAll();
        }

        exchange.getIn().setBody(people);
    }

    public Response<Person> insertPerson(Exchange exchange) {
        Person person = null;
        //  Person person = personRepository.insert(exchange.getIn().getBody(Person.class));
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, "201");
        return CreateResponseUtil.createResponse(person, "Successful creation", "201");
    }

      public Object getPerson(@Header("id") String id) {
          return null;
      }

    public List<Person> getPeopleByFirstName(@Header("firstName") String firstName) {
        return personRepository.findByFirstName(firstName);
    }

    public List<Person> getPeopleByLastName(@Header("lastName") String lastName) {
        return personRepository.findByLastName(lastName);
    }

}
