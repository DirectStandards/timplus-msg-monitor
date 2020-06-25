package org.directtruststandards.timplus.monitor.condition;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.directtruststandards.timplus.monitor.tx.model.TxDetail;
import org.directtruststandards.timplus.monitor.tx.model.TxDetailType;
import org.directtruststandards.timplus.monitor.tx.model.TxStanzaType;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

public abstract class AbstractReleaseStrategy implements TxReleaseStrategy
{
	public AbstractReleaseStrategy()
	{
		super();
	}
	
	public static Tx getStanzaToTrack(Collection<Tx> txs)
	{
		if (txs == null)
			return null;
		
		for (Tx tx : txs)
		{
			// The original message has a to, from, msg id, type chat or groupchat, and has a stanza type of Message
			if (tx.getStanzaType() == TxStanzaType.MESSAGE)
			{
				TxDetail detail = tx.getDetail(TxDetailType.RECIPIENTS);
				final String to = (detail != null) ? detail.getDetailValue() : "";
				
				detail = tx.getDetail(TxDetailType.FROM);
				final String from = (detail != null) ? detail.getDetailValue() : "";
				
				detail = tx.getDetail(TxDetailType.TYPE);
				final String type = (detail != null) ? detail.getDetailValue() : "";
				
				if (!StringUtils.isEmpty(to) && !StringUtils.isEmpty(from) && !StringUtils.isEmpty(type) &&
						(type.compareToIgnoreCase(org.jivesoftware.smack.packet.Message.Type.chat.name()) == 0 || 
					     type.compareToIgnoreCase(org.jivesoftware.smack.packet.Message.Type.groupchat.name()) == 0))
				{
					return tx;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public boolean accept(Message<?> message)
	{
		if (message == null || !(message.getPayload() instanceof Collection))
			return false;
		
		@SuppressWarnings("unchecked")
		final Collection<Tx> txs = (Collection<Tx>)message.getPayload();
		
		// if the aggregation is complete (i.e. all recipients have responses), then discard it
		// otherwise it will need to go on for further processing so error messages can be generated
		return !isComplete(txs);
	}

	@Override
	public boolean canRelease(MessageGroup group)
	{
		if (group == null)
			return false;
		
		final Collection<Tx> txs = new ArrayList<>();
		for (Message<?> msg : group.getMessages())
			if (msg.getPayload() instanceof Tx)
				txs.add(Tx.class.cast(msg.getPayload()));
			
		return isComplete(txs);
	}
}
