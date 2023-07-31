package org.example.config;

import com.mongodb.client.MongoClients;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.main.Main;
import org.example.route.MongoDBRoute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUrl;

    @Bean("mongoBean")
    public void mongoBean(){
        Main main = new Main();
        main.bind("mongoBean", MongoClients.create(mongoUrl));
        CamelContext context = new DefaultCamelContext();
        main.configure().addRoutesBuilder(new MongoDBRoute(context));

    }

}
