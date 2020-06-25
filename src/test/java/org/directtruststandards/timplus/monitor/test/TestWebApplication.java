package org.directtruststandards.timplus.monitor.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan( basePackages= {"org.directtruststandards.timplus.monitor.resource", "org.directtruststandards.timplus.monitor.streams"})
@Import(RouteConfigurations.AggregatorWithErrorGenerationRoute.class)
public class TestWebApplication
{
    public static void main(String[] args) 
    {
        SpringApplication.run(TestWebApplication.class, args);
    }  
}
