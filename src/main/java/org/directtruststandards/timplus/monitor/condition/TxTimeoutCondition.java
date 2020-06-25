package org.directtruststandards.timplus.monitor.condition;


import org.springframework.integration.store.MessageGroup;

public interface TxTimeoutCondition
{
	public long getTimeout(MessageGroup group);
}
