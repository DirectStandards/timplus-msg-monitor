package org.directtruststandards.timplus.monitor.route;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.UUID;

import org.directtruststandards.timplus.monitor.condition.TxReleaseStrategy;
import org.directtruststandards.timplus.monitor.test.RouteConfigurations;
import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=RouteConfigurations.AggregatorOnlyRoute.class)
@TestPropertySource(locations = "classpath:properties/normalChatMonitor.properties")
public class MonitorChatMessageTest
{
	@Autowired
	protected DirectChannel inputChannel;
	
	@Autowired
	protected QueueChannel receive;
	
	@Autowired 
	protected TxReleaseStrategy releaseStrategy;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCompleteChat_ampResponse_assertComplete() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name())).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeAMPStanza(originalMsgId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com")).build());
		
		final Message<?> msg = receive.receive(1000);
		assertNotNull(msg);
		
		assertTrue(releaseStrategy.isComplete((Collection<Tx>) msg.getPayload()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCompleteChat_errorResponse_assertComplete() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name())).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeErrorMessageStanza(originalMsgId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com")).build());
		
		final Message<?> msg = receive.receive(1000);
		assertNotNull(msg);
		
		assertTrue(releaseStrategy.isComplete((Collection<Tx>) msg.getPayload()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCompleteGroupChat_ampResponse_assertComplete() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.groupchat.name())).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeAMPStanza(originalMsgId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com")).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeAMPStanza(originalMsgId, "me@you.com", 
				"gm2552@cerner.com", "", "me@you.com")).build());
		
		final Message<?> msg = receive.receive(1000);
		assertNotNull(msg);
		
		assertTrue(releaseStrategy.isComplete((Collection<Tx>) msg.getPayload()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCompleteGroupChat_errorResponse_assertComplete() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.groupchat.name())).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeErrorMessageStanza(originalMsgId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com")).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeErrorMessageStanza(originalMsgId, "me@you.com", 
				"gm2552@cerner.com", "", "me@you.com")).build());
		
		final Message<?> msg = receive.receive(1000);
		assertNotNull(msg);
		
		assertTrue(releaseStrategy.isComplete((Collection<Tx>) msg.getPayload()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCompleteGroupChat_errorAndAMPResponse_assertComplete() throws Exception
	{
		final String originalMsgId = UUID.randomUUID().toString();
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeMessageStanza(originalMsgId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com,me@you.com", org.jivesoftware.smack.packet.Message.Type.groupchat.name())).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeErrorMessageStanza(originalMsgId, "gm2552@direct.securehealthemail.com", 
				"gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com")).build());
		
		inputChannel.send(MessageBuilder.withPayload(TestUtils.makeAMPStanza(originalMsgId, "me@you.com", 
				"gm2552@cerner.com", "", "me@you.com")).build());
		
		final Message<?> msg = receive.receive(1000);
		assertNotNull(msg);
		
		assertTrue(releaseStrategy.isComplete((Collection<Tx>) msg.getPayload()));
	}
}
