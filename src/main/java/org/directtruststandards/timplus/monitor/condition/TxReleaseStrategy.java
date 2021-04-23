package org.directtruststandards.timplus.monitor.condition;

import java.util.Collection;

import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.core.MessageSelector;

public interface TxReleaseStrategy extends ReleaseStrategy, MessageSelector
{
	public boolean isComplete(Collection<Tx> txs);
	
	public Collection<String> getIncompleteRecipients(Collection<Tx> txs);
}
