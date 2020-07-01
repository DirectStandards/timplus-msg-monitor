package org.directtruststandards.timplus.monitor.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.directtruststandards.timplus.monitor.condition.AbstractReleaseStrategy;
import org.directtruststandards.timplus.monitor.condition.TxReleaseStrategy;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.directtruststandards.timplus.monitor.tx.model.TxDetailType;
import org.jivesoftware.smack.packet.StanzaError;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;

public class MonitorToErrorMessageTransformer extends AbstractTransformer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorToErrorMessageTransformer.class);
	
	protected TxReleaseStrategy releaseStr; 
	protected String descriptiveText;
	
	public MonitorToErrorMessageTransformer(TxReleaseStrategy releaseStr, String descriptiveText)
	{
		super();
		
		this.releaseStr = releaseStr;
		this.descriptiveText = descriptiveText;
	}

	@Override
	protected Object doTransform(Message<?> message)
	{
		
		if (message == null || !(message.getPayload() instanceof Collection))
			return Collections.emptyList();
		
		@SuppressWarnings("unchecked")
		final Collection<Tx> txs = (Collection<Tx>)message.getPayload();
		
		// get the original message so we know who need to generate error messages for
		final Tx originalTx = AbstractReleaseStrategy.getStanzaToTrack(txs);
		
		if (originalTx == null)
			return Collections.emptyList();
		
		// make sure we have incomplete recipients
		final Collection<String> incompleteRecips = releaseStr.getIncompleteRecipients(txs);
		
		if(incompleteRecips == null || incompleteRecips.isEmpty())
			return Collections.emptyList();
		
		final Collection<String> errorStanzas = new ArrayList<>();
		
		// generate a stanza per incomplete recipient
		for (String incompleteRecip : incompleteRecips)
		{
			try
			{
				// Create the base message with the required attributes
				final Jid to = JidCreate.from(originalTx.getDetail(TxDetailType.FROM).getDetailValue());
				final Jid from = JidCreate.from(incompleteRecip);
				
				final org.jivesoftware.smack.packet.Message msgStanza = 
						new org.jivesoftware.smack.packet.Message(to, org.jivesoftware.smack.packet.Message.Type.error);
				
				msgStanza.setStanzaId(originalTx.getDetail(TxDetailType.MSG_ID).getDetailValue());
				msgStanza.setFrom(from);
				
				// Add the error extension
				final StanzaError error = StanzaError.from(StanzaError.Condition.remote_server_timeout, descriptiveText)
				  .setType(StanzaError.Type.WAIT).setStanza(msgStanza).build();
				

				msgStanza.addExtension(error);
				
				errorStanzas.add(msgStanza.toXML(null).toString());
			}
			catch (Exception e)
			{
				LOGGER.warn("Failed to create an error message to be sent to " + incompleteRecip, e);
			}
		}
		
		return errorStanzas;
	}
	
	
}
