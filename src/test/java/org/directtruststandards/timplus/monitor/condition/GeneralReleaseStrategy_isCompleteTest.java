package org.directtruststandards.timplus.monitor.condition;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.junit.jupiter.api.Test;

public class GeneralReleaseStrategy_isCompleteTest 
{
	
	@Test
	public void testIsComplete_nullTxs_assertFalse()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		assertFalse(condition.isComplete(null));
	}
	
	@Test
	public void testIsComplete_nullEmptyTx_assertFalse()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		assertFalse(condition.isComplete(new ArrayList<Tx>()));
	}
	
	@Test
	public void testIsComplete_noMessageToTrack_assertFalse()
	{
		GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		final Tx tx = TestUtils.makeAMPStanza("123", "", "", "", "");
		final Collection<Tx> txs = Arrays.asList(tx);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_noRecips_assertFalse()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		final Tx tx = TestUtils.makeMessageStanza(UUID.randomUUID().toString(), "", "", "");

		final List<Tx> txs = Arrays.asList(tx);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_ampRecipNotAnOriginalRecips_assertFalse()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMessageId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		
		// AMP to original message
		final Tx ampMessage = TestUtils.makeAMPStanza(originalMessageId, "ah4626@direct.securehealthemail.com", "gm2552@cerner.com", "", "ah4626@direct.securehealthemail.com");
		
		final List<Tx> txs = Arrays.asList(originalMessage, ampMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_errorRecipNotAnOriginalRecips_assertFalse()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMessageId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name());

		// Error to original message
		final Tx errorMessage = TestUtils.makeErrorMessageStanza(originalMessageId, "ah4626@direct.securehealthemail.com", "gm2552@cerner.com", "", "ah4626@direct.securehealthemail.com");
		
		final List<Tx> txs = Arrays.asList(originalMessage, errorMessage);
		
		assertFalse(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_ampMessageReceived_assertTrue()
	{
		final GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMessageId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name());

		// amp to original message		
		final Tx ampMessage = TestUtils.makeAMPStanza(originalMessageId,"gm2552@direct.securehealthemail.com", "gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com");
		
		final List<Tx> txs = Arrays.asList(originalMessage, ampMessage);
		
		assertTrue(condition.isComplete(txs));
	}
	
	
	@Test
	public void testIsComplete_errorMessageReceived_assertTrue()
	{
		GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMessageId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name());

		// Error to original message
		final Tx errorMessage = TestUtils.makeErrorMessageStanza(originalMessageId,"gm2552@direct.securehealthemail.com", "gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com");
		
		final List<Tx> txs = Arrays.asList(originalMessage, errorMessage);
		
		assertTrue(condition.isComplete(txs));
	}
	
	@Test
	public void testIsComplete_groupChat_errorMessagesReceived_assertTrue()
	{
		GeneralReleaseStrategy condition = new GeneralReleaseStrategy();
		
		// original message
		final String originalMessageId = UUID.randomUUID().toString();	
		
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMessageId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.groupchat.name());

		// Error to original message
		final Tx errorMessage1 = TestUtils.makeErrorMessageStanza(originalMessageId,"gm2552@direct.securehealthemail.com", "gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com");
		final Tx errorMessage2 = TestUtils.makeErrorMessageStanza(originalMessageId,"me@you.com", "gm2552@cerner.com", "", "me@you.com");
		
		final List<Tx> txs = Arrays.asList(originalMessage, errorMessage1, errorMessage2);
		
		assertTrue(condition.isComplete(txs));
	}
}
