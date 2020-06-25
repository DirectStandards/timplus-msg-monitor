package org.directtruststandards.timplus.monitor.condition;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.junit.Test;

public class AbstractReleaseStrategy_getMessageToTrackTest 
{

		
	@Test
	public void testGetMessageToTrack_nullTx_assertNull()
	{		
		assertNull(AbstractReleaseStrategy.getStanzaToTrack(null));
	}
	
	@Test
	public void testGetMessageToTrack_emptyTx_assertNull()
	{
		
		assertNull(AbstractReleaseStrategy.getStanzaToTrack(new ArrayList<Tx>()));
	}
	
	@Test
	public void testGetMessageToTrack_noMessage_assertNull()
	{
		
		final Tx tx = TestUtils.makeErrorMessageStanza("12345", "gmeyer@cenrer.com", "me@you.com", "", "gmeyer@cenrer.com");
		List<Tx> txs = Arrays.asList(tx);
		
		assertNull(AbstractReleaseStrategy.getStanzaToTrack(txs));
	}
	
	@Test
	public void testGetMessageToTrack_chatType_assertMessageFound()
	{
		
		final Tx tx = TestUtils.makeMessageStanza("12345", "gmeyer@cenrer.com", "me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		List<Tx> txs = Arrays.asList(tx);
		
		Tx foundTx = AbstractReleaseStrategy.getStanzaToTrack(txs);
		
		assertEquals(tx, foundTx);
	}
	
	@Test
	public void testGetMessageToTrack_groupChatType_assertMessageFound()
	{
		
		final Tx tx = TestUtils.makeMessageStanza("12345", "gmeyer@cenrer.com", "me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		List<Tx> txs = Arrays.asList(tx);
		
		Tx foundTx = AbstractReleaseStrategy.getStanzaToTrack(txs);
		
		assertEquals(tx, foundTx);
	}
	
	@Test
	public void testGetMessageToTrack_normalChatType_assertNull()
	{
		
		final Tx tx = TestUtils.makeMessageStanza("12345", "gmeyer@cenrer.com", "me@you.com", org.jivesoftware.smack.packet.Message.Type.normal.name());
		List<Tx> txs = Arrays.asList(tx);
		
		assertNull(AbstractReleaseStrategy.getStanzaToTrack(txs));
	}
}
