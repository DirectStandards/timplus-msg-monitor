package org.directtruststandards.timplus.monitor.route;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.UUID;

import org.directtruststandards.timplus.monitor.condition.TxReleaseStrategy;
import org.directtruststandards.timplus.monitor.test.RouteConfigurations;
import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=RouteConfigurations.AggregatorWithErrorGenerationRoute.class)
@TestPropertySource(locations = "classpath:properties/timeoutChatMonitor.properties")
public class ErrorMessageGenerationTest
{
	@Autowired
	@Qualifier("monitorStart")
	protected DirectChannel inputChannel;
	
	@Autowired
	protected QueueChannel receive;

	@Autowired 
	protected TxReleaseStrategy releaseStrategy;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIncompleteChat_singleRecipient_assertSingleIncompleteRecip() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name())).build());
		
		final Message<?> msg = receive.receive(2000);

		final Collection<String> errors = (Collection<String>)msg.getPayload();
		
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIncompleteChat_multipleRecipient_assertMutipleIncompleteRecip() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.groupchat.name())).build());
		
		final Message<?> msg = receive.receive(2000);

		final Collection<String> errors = (Collection<String>)msg.getPayload();
		
		assertEquals(2, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIncompleteChat_multipleRecipient_singleAMPeEssage_assertSingleIncompleteRecip() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name())).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeAMPStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", "", "me@you.com")).build());
		
		final Message<?> msg = receive.receive(2000);

		final Collection<String> errors = (Collection<String>)msg.getPayload();
		
		assertEquals(1, errors.size());
		//assertEquals("gm2552@direct.securehealthemail.com", recips.iterator().next());
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIncompleteChat_multipleRecipient_singleErrorMessage_assertSingleIncompleteRecip() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.chat.name())).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeErrorMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", "", "me@you.com")).build());
		
		final Message<?> msg = receive.receive(2000);

		final Collection<String> errors = (Collection<String>)msg.getPayload();
		
		assertEquals(1, errors.size());
		//assertEquals("gm2552@direct.securehealthemail.com", recips.iterator().next());
	}
}
