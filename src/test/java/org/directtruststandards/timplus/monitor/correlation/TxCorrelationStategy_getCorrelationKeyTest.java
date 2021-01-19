package org.directtruststandards.timplus.monitor.correlation;

import static org.junit.Assert.assertEquals;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.directtruststandards.timplus.monitor.tx.model.TxDetail;
import org.directtruststandards.timplus.monitor.tx.model.TxDetailType;
import org.directtruststandards.timplus.monitor.tx.model.TxStanzaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;


public class TxCorrelationStategy_getCorrelationKeyTest 
{
	@Test
	public void testEvaluate_emptyDetails_assertException()
	{
		Assertions.assertThrows(IllegalStateException.class, () ->
		{
			final TxCorrelationStategy exp = new TxCorrelationStategy();
			
			final Tx tx = new Tx(TxStanzaType.MESSAGE, new HashMap<TxDetailType, TxDetail>());
			
			final Message<?> msg = new GenericMessage<Tx>(tx);
			
			exp.getCorrelationKey(msg);
		});
	}
	
	@Test
	public void testEvaluate_messageStanza_noMsgId_assertException()
	{
		Assertions.assertThrows(IllegalStateException.class, () ->
		{
			final TxCorrelationStategy exp = new TxCorrelationStategy();
			
			final Tx tx = TestUtils.makeMessageStanza("", "me@test.com", "you@test.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
			
			final Message<?> msg = new GenericMessage<Tx>(tx);
			
			exp.getCorrelationKey(msg);
		});
	}

	@Test
	public void testEvaluate_messageStanza_emptyMsgId_assertException()
	{
		Assertions.assertThrows(IllegalStateException.class, () ->
		{
			final TxCorrelationStategy exp = new TxCorrelationStategy();
			
			final Map<TxDetailType, TxDetail> details = new HashMap<>();
			details.put(TxDetailType.MSG_ID, new TxDetail(TxDetailType.MSG_ID, ""));
			
			final Tx tx = new Tx(TxStanzaType.MESSAGE, details);
			
			final Message<?> msg = new GenericMessage<Tx>(tx);
			
			exp.getCorrelationKey(msg);
		});
	}
	
	@Test
	public void testEvaluate_invalidPayloadType_assertException()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () ->
		{
			final TxCorrelationStategy exp = new TxCorrelationStategy();
			
			final Message<?> msg = new GenericMessage<Object>(new Object());
			
			exp.getCorrelationKey(msg);
		});
	}
	
	@Test
	public void testEvaluate_messageStanza_msgIdExists_assertMessageIdEvaluated()
	{
		final TxCorrelationStategy exp = new TxCorrelationStategy();
		
		final String msgId = UUID.randomUUID().toString();
		final Tx tx = TestUtils.makeMessageStanza(msgId, "me@test.com", "you@test.com", org.jivesoftware.smack.packet.Message.Type.chat.name());

		final Message<?> msg = new GenericMessage<Tx>(tx);
		
		assertEquals(msgId, exp.getCorrelationKey(msg));
	}
	
}
