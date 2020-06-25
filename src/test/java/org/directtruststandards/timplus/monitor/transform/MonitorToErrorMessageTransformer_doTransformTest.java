package org.directtruststandards.timplus.monitor.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.directtruststandards.timplus.monitor.condition.GeneralReleaseStrategy;
import org.directtruststandards.timplus.monitor.condition.TxReleaseStrategy;
import org.directtruststandards.timplus.monitor.impl.DefaultTxParser;
import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.directtruststandards.timplus.monitor.tx.model.TxDetailType;
import org.directtruststandards.timplus.monitor.tx.model.TxStanzaType;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class MonitorToErrorMessageTransformer_doTransformTest
{
	protected TxReleaseStrategy releaseStr = new GeneralReleaseStrategy();
	
	@Test
	public void testWrongPayloadType_assertEmptyCollection() throws Exception
	{
		final Message<?> msg = MessageBuilder.withPayload(new Object()).build();
		
		final MonitorToErrorMessageTransformer trans = new MonitorToErrorMessageTransformer(releaseStr, "");
		
		@SuppressWarnings("unchecked")
		final Collection<String> errors = (Collection<String>)trans.doTransform(msg);
		
		assertTrue(errors.isEmpty());
	}
	
	@Test
	public void testNoOriginalMsg_assertEmptyCollection() throws Exception
	{
		final Message<?> msg = MessageBuilder.withPayload(Collections.emptyList()).build();
		
		final MonitorToErrorMessageTransformer trans = new MonitorToErrorMessageTransformer(releaseStr, "");
		
		@SuppressWarnings("unchecked")
		final Collection<String> errors = (Collection<String>)trans.doTransform(msg);
		
		assertTrue(errors.isEmpty());
	}
	
	@Test
	public void testNoIncompleteRecips_assertEmptyCollection() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMsgId, "gmeyer@cenrer.com", "me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		final Tx ampMessage = TestUtils.makeAMPStanza(originalMsgId, "me@you.com", "gmeyer@cenrer.com", org.jivesoftware.smack.packet.Message.Type.chat.name(), "me@you.com");
		
		final Message<?> msg = MessageBuilder.withPayload(Arrays.asList(originalMessage, ampMessage)).build();
		
		final MonitorToErrorMessageTransformer trans = new MonitorToErrorMessageTransformer(releaseStr, "");
		
		@SuppressWarnings("unchecked")
		final Collection<String> errors = (Collection<String>)trans.doTransform(msg);
		
		assertTrue(errors.isEmpty());
	}
	
	@Test
	public void testSingleIncompleteRecip_assertSingleErrorMessage() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMsgId, "gmeyer@cenrer.com", "me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		
		final Message<?> msg = MessageBuilder.withPayload(Arrays.asList(originalMessage)).build();
		
		final MonitorToErrorMessageTransformer trans = new MonitorToErrorMessageTransformer(releaseStr, "");
		
		@SuppressWarnings("unchecked")
		final Collection<String> errors = (Collection<String>)trans.doTransform(msg);
		
		assertEquals(1, errors.size());
		
		final DefaultTxParser parser = new DefaultTxParser();
		final Tx tx = parser.parseStanza(errors.iterator().next());
		
		assertEquals(TxStanzaType.MESSAGE_ERROR, tx.getStanzaType());
		assertEquals("me@you.com", tx.getDetail(TxDetailType.FROM).getDetailValue());
		assertEquals("gmeyer@cenrer.com", tx.getDetail(TxDetailType.RECIPIENTS).getDetailValue());
		assertEquals("error", tx.getDetail(TxDetailType.TYPE).getDetailValue());		
	}
	
	@Test
	public void testMutilpleIncompleteRecip_assertMutipleErrorMessages() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMsgId, "gmeyer@cenrer.com", 
				"me@you.com,test@you.com", org.jivesoftware.smack.packet.Message.Type.groupchat.name());
		
		final Message<?> msg = MessageBuilder.withPayload(Arrays.asList(originalMessage)).build();
		
		final MonitorToErrorMessageTransformer trans = new MonitorToErrorMessageTransformer(releaseStr, "");
		
		@SuppressWarnings("unchecked")
		final Collection<String> errors = (Collection<String>)trans.doTransform(msg);
		
		assertEquals(2, errors.size());	
	}
}
