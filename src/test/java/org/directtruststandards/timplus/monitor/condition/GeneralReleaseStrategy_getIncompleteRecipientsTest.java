package org.directtruststandards.timplus.monitor.condition;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.junit.jupiter.api.Test;

public class GeneralReleaseStrategy_getIncompleteRecipientsTest 
{
	@Test
	public void testIsComplete_nullTxs_assertEmptyList()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final Collection<String> recips = condition.getIncompleteRecipients(null);
		
		assertEquals(0, recips.size());
	}
	
	@Test
	public void testIsComplete_nullTxs_emptyTxs()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final Collection<String> recips = condition.getIncompleteRecipients(new ArrayList<Tx>());
		
		assertEquals(0, recips.size());
	}
	
	@Test
	public void testIsComplete_noMessageToTrack_emptyTxs()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final Tx originalMessage = TestUtils.makeErrorMessageStanza(UUID.randomUUID().toString(), "gm2552@cerner.com", "gm2552@direct.securehealthemail.com", "", "");

		final Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		final Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(0, recips.size());
	}
	
	@Test
	public void testIsComplete_noRecips_emptyTxs()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final Tx originalMessage =  TestUtils.makeMessageStanza(UUID.randomUUID().toString(), "gm2552@cerner.com", "", "");
		
		final Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		final Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(0, recips.size());
	}
	
	@Test
	public void testIsComplete_multipleRecips_noResponseMessages_multipleIncomplete()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final Tx originalMessage =  TestUtils.makeMessageStanza(UUID.randomUUID().toString(), 
				"gm2552@cerner.com", "test@you.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		
		final Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		final Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(2, recips.size());
	}
	
	@Test
	public void testIsComplete_multipleRecips_errorMessage_singleIncomplete()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final String msgId = UUID.randomUUID().toString();
		
		final Tx originalMessage =  TestUtils.makeMessageStanza(msgId, 
				"gm2552@cerner.com", "test@you.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		
		final Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		final Tx errorMessage =  TestUtils.makeErrorMessageStanza(msgId, "test@you.com", "gm2552@cerner.com", "", "test@you.com");
		txs.add(errorMessage);
		
		final Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(1, recips.size());
		
		assertEquals("me@you.com", recips.iterator().next());
	}
	
	@Test
	public void testIsComplete_multipleRecips_apmMessage_singleIncomplete()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final String msgId = UUID.randomUUID().toString();
		
		final Tx originalMessage =  TestUtils.makeMessageStanza(msgId, 
				"gm2552@cerner.com", "test@you.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		
		final Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		final Tx ampMessage =  TestUtils.makeAMPStanza(msgId, "test@you.com", "gm2552@cerner.com", "", "test@you.com");
		txs.add(ampMessage);
		
		final Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(1, recips.size());
		
		assertEquals("me@you.com", recips.iterator().next());
	}
	
	@Test
	public void testIsComplete_multipleRecips_apmAndErrorMessage_noneIncomplete()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final String msgId = UUID.randomUUID().toString();
		
		final Tx originalMessage =  TestUtils.makeMessageStanza(msgId, 
				"gm2552@cerner.com", "test@you.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		
		final Collection<Tx> txs = new ArrayList<Tx>();
		txs.add(originalMessage);
		
		final Tx errorMessage =  TestUtils.makeErrorMessageStanza(msgId, "test@you.com", "gm2552@cerner.com", "", "test@you.com");
		txs.add(errorMessage);
		
		final Tx ampMessage =  TestUtils.makeAMPStanza(msgId, "me@you.com", "gm2552@cerner.com", "", "me@you.com");
		txs.add(ampMessage);
		
		final Collection<String> recips = condition.getIncompleteRecipients(txs);
		
		assertEquals(0, recips.size());
	}
}
