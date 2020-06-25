package org.directtruststandards.timplus.monitor.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.directtruststandards.timplus.monitor.tx.model.TxDetail;
import org.directtruststandards.timplus.monitor.tx.model.TxDetailType;
import org.directtruststandards.timplus.monitor.tx.model.TxStanzaType;

public class GeneralReleaseStrategy extends AbstractReleaseStrategy
{
	public GeneralReleaseStrategy()
	{
		super();
	}
	
	public boolean isComplete(Collection<Tx> txs)
	{
		if (txs == null || txs.isEmpty())
			return false;
		
		final Tx originalStanza = getStanzaToTrack(txs);
		if (originalStanza == null)
			return false;
		
		final TxDetail originalRecipDetail = originalStanza.getDetail(TxDetailType.RECIPIENTS);
		if (originalRecipDetail == null)
			return false;	
		 
		final Collection<String> incompleteRecips = getIncompleteRecipients(txs);
		
		return incompleteRecips.isEmpty();
	}
	
	@SuppressWarnings("incomplete-switch")
	public Collection<String> getIncompleteRecipients(Collection<Tx> txs)
	{
		if (txs == null || txs.isEmpty())
			return Collections.emptyList();
		
		final Tx originalStanza = getStanzaToTrack(txs);
		if (originalStanza == null)
			return Collections.emptyList();
		
		final TxDetail originalRecipDetail = originalStanza.getDetail(TxDetailType.RECIPIENTS);
		if (originalRecipDetail == null)
			return Collections.emptyList();
		
		// add the original recipient list to a map of recipients to status
		final Map<String, RecipientResponseStatus> recipStatuses = new HashMap<String, RecipientResponseStatus>();
		for (String recip : originalRecipDetail.getDetailValue().split(","))
			recipStatuses.put(recip.trim(), new RecipientResponseStatus(recip.trim()));
		
		for (Tx tx : txs)
		{
			final TxDetail originalRecip = tx.getDetail(TxDetailType.ORIGINAL_RECIPIENT);
			if (originalRecipDetail != null)
			{
				switch (tx.getStanzaType())
				{
				   // AMP and Error messages are sent per recipient, so we will 
				   // extract the recipient and mark their status
				   case AMP:
				   case MESSAGE_ERROR:
				   {
					   final RecipientResponseStatus recipStatus = recipStatuses.get(originalRecip.getDetailValue().trim());
					   if (recipStatus != null)
						   recipStatus.addReceivedStatus((tx.getStanzaType() == TxStanzaType.AMP) 
								   ? RecipientResponseStatus.AMPReceived : RecipientResponseStatus.ErrorReceived);
					   break;
				   }

				}
			}
		}
		
		
		final Collection<String> retVal = new ArrayList<String>();
		
		// only mark as complete if the recipient
		// has received some type of notification
		// if no status has been received, then add them
		// to the list of incomplete recipients
		for (RecipientResponseStatus status : recipStatuses.values())
			if (status.getReceivedStatus() == 0)
			{
				retVal.add(status.getRecipient());
			}	
		
		return retVal;		
	}
	
	private static class RecipientResponseStatus
	{
		public static final short AMPReceived = 0x0001;
		public static final short ErrorReceived = 0x0002;
		
		protected int statusesReceived = 0;
		protected final String recipient;
		
		RecipientResponseStatus(String recipient)
		{
			this.recipient = recipient;
		}
		
		public void addReceivedStatus(short status)
		{
			statusesReceived |= status;
		}
		
		public int getReceivedStatus()
		{
			return statusesReceived;
		}
		
		public String getRecipient()
		{
			return recipient;
		}
	}
}
