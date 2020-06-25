package org.directtruststandards.timplus.monitor.spring;

import javax.sql.DataSource;

import org.directtruststandards.timplus.monitor.condition.DecayingTimeoutCondition;
import org.directtruststandards.timplus.monitor.condition.GeneralReleaseStrategy;
import org.directtruststandards.timplus.monitor.condition.TxReleaseStrategy;
import org.directtruststandards.timplus.monitor.condition.TxTimeoutCondition;
import org.directtruststandards.timplus.monitor.correlation.TxCorrelationStategy;
import org.directtruststandards.timplus.monitor.transform.MonitorToErrorMessageTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.store.JdbcMessageStore;
import org.springframework.integration.store.MessageGroupStore;
import org.springframework.integration.store.MessageGroupStoreReaper;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.transformer.Transformer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Configuration
@EnableIntegration
@EnableScheduling
@Component
public class RouteComponents
{	
	@Value("${timplus.monitor.condition.generalConditionTimeout:3600000}")
	protected long generalTimeout;
	
	@Value("${timplus.monitor.transformation.errorText:No response for message status within allocated time.}")
	protected String errorText;
	
	@Value("${timplus.monitor.condition.reaper.timeout:86400000}")
	protected long reaperTimeout;
	
	@Value("${timplus.monitor.condition.reaper.period:3600000}")
	protected long reaperPeriod;
	
	@Bean
	public TxReleaseStrategy releaseStrategy()
	{
		return new GeneralReleaseStrategy();
	}
	
	@Bean
	public CorrelationStrategy correlationStradegy()
	{
		return new TxCorrelationStategy();
	}	
	
	@Bean
	public MessageGroupStore messageGroupStore(DataSource dataSource)
	{
		final JdbcMessageStore store = new JdbcMessageStore(dataSource);
		return store;
	}
	
	@Bean
	public TxTimeoutCondition timeoutCondition()
	{
		return new DecayingTimeoutCondition(generalTimeout);
	}
	
	@Bean
	public Transformer errorTransformer()
	{
		return new MonitorToErrorMessageTransformer(releaseStrategy(), errorText);
	}
	
	@Bean
	public LockRegistry lockRegistry(DataSource dataSource)
	{
		return new JdbcLockRegistry(new DefaultLockRepository(dataSource));
	}
	
	@Bean 
	public MessageGroupStoreReaper messageGroupStoreReaper(MessageGroupStore store)
	{
		final MessageGroupStoreReaper reaper = new MessageGroupStoreReaper();
		reaper.setMessageGroupStore(store);
		reaper.setTimeout(reaperTimeout);
		
		return reaper;
	}
}
