package org.directtruststandards.timplus.monitor.correlation;

import org.apache.commons.lang3.StringUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.directtruststandards.timplus.monitor.tx.model.TxDetail;
import org.directtruststandards.timplus.monitor.tx.model.TxDetailType;
import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.messaging.Message;

public class TxCorrelationStategy implements CorrelationStrategy
{
	public TxCorrelationStategy ()
	{
		super();
	}

	@Override
	public Object getCorrelationKey(Message<?> message)
	{
		if (!(message.getPayload() instanceof Tx))
			throw new IllegalArgumentException("Monitoring payload must be of type TX.");
		
		final Tx tx = Tx.class.cast(message.getPayload());
		
		// The message id attribute is our correlation id.  This is the true even for
		// error and AMP messages as their message id attribute is the same as the message id
		// of the original message
		final TxDetail msgIdDetail = tx.getDetail(TxDetailType.MSG_ID);
		
		if (msgIdDetail == null)
			throw new IllegalStateException("Monitoring payload must contain a message id attribute.");
		
		if (StringUtils.isEmpty(msgIdDetail.getDetailValue()))
			throw new IllegalStateException("Message id must not be null or empty.");
		
		return msgIdDetail.getDetailValue();
	}
}
