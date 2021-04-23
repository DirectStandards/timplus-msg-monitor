package org.directtruststandards.timplus.monitor.test;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.MessageChannels;

@EnableAutoConfiguration
@EnableIntegration
@Configuration
public class RouteChannelConfiguration
{	
	@Bean
	public DirectChannel monitorStart() 
	{
	    return MessageChannels.direct("monitorStart")
	                        .get();
	}
	
	@Bean
	public QueueChannel monitorOut() 
	{
	    return MessageChannels.queue("monitorOut")
	                        .get();
	}
}
