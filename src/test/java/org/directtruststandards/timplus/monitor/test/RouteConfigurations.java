package org.directtruststandards.timplus.monitor.test;

import org.directtruststandards.timplus.monitor.condition.TxReleaseStrategy;
import org.directtruststandards.timplus.monitor.condition.TxTimeoutCondition;
import org.directtruststandards.timplus.monitor.spring.RouteComponents;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.store.MessageGroupStore;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.transformer.Transformer;

public class RouteConfigurations
{

	@Import({RouteComponents.class, RouteChannelConfiguration.class})
	@Configuration
	public static class AggregatorOnlyRoute
	{		
		@Bean
		public IntegrationFlow monitorFlow(DirectChannel inputChannel, QueueChannel receive, CorrelationStrategy correlationStradegy, 
				TxReleaseStrategy releaseStrategy, TxTimeoutCondition timeoutCondition, MessageGroupStore messageGroupStore, 
				LockRegistry lockRegistry)
		{
			return IntegrationFlows.from(inputChannel)
			.aggregate(a -> a.correlationStrategy(correlationStradegy)
			       .releaseStrategy(releaseStrategy)
			       .groupTimeout(g -> timeoutCondition.getTimeout(g))
			       .sendPartialResultOnExpiry(true)
			       .messageStore(messageGroupStore))	
			.channel(receive)
			.get();
		}	
	}
	
	@Import({RouteComponents.class, RouteChannelConfiguration.class})
	@Configuration
	public static class AggregatorWithCompleteFilterRoute
	{		
		@Bean
		public IntegrationFlow monitorFlow(@Qualifier("monitorStart") DirectChannel inputChannel, QueueChannel receive, CorrelationStrategy correlationStradegy, 
				TxReleaseStrategy releaseStrategy, TxTimeoutCondition timeoutCondition, MessageGroupStore messageGroupStore,
				LockRegistry lockRegistry)
		{
			return IntegrationFlows.from(inputChannel)
			.aggregate(a -> a.correlationStrategy(correlationStradegy)
			       .releaseStrategy(releaseStrategy)
			       .groupTimeout(g -> timeoutCondition.getTimeout(g))
			       .sendPartialResultOnExpiry(true)
			       .messageStore(messageGroupStore))	
			.filter(releaseStrategy)
			.channel(receive)
			.get();
		}	
	}
	
	@Import({RouteComponents.class, RouteChannelConfiguration.class})
	@Configuration
	public static class AggregatorWithErrorGenerationRoute
	{		
		@Bean
		public IntegrationFlow monitorFlow(@Qualifier("monitorStart") DirectChannel inputChannel, QueueChannel receive, CorrelationStrategy correlationStradegy, 
				TxReleaseStrategy releaseStrategy, TxTimeoutCondition timeoutCondition, MessageGroupStore messageGroupStore, 
				LockRegistry lockRegistry, Transformer transformer)
		{
			return IntegrationFlows.from(inputChannel)
			.aggregate(a -> a.correlationStrategy(correlationStradegy)
			       .releaseStrategy(releaseStrategy)
			       .groupTimeout(g -> timeoutCondition.getTimeout(g))
			       .sendPartialResultOnExpiry(true)
			       .messageStore(messageGroupStore))	
			.filter(releaseStrategy)
			.transform(transformer)
			.channel(receive)
			.get();
		}	
	}
}
