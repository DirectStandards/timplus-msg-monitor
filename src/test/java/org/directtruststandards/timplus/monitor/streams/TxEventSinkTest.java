package org.directtruststandards.timplus.monitor.streams;

import java.util.Collection;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.directtruststandards.timplus.monitor.test.TestWebApplication;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestWebApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@TestPropertySource(locations = "classpath:properties/timeoutChatMonitor.properties")
public class TxEventSinkTest
{
	@Autowired
	private TxInput streamChannel;
	
	@Autowired
	protected QueueChannel receive;
	
	@Autowired
	@Qualifier("monitorStart")
	private MessageChannel integrationInputChannel;
	
	@Autowired 
	private ObjectMapper mapper;
	
	@Autowired
	private TxEventSink sink;
	
	@Test
	public void testSendTxToSink() throws Exception
	{
		final MessageChannel spyProducer = spy(integrationInputChannel);
	
		sink.setMessageChannel(spyProducer);
		
		// send original message
		final String originalMessageId = UUID.randomUUID().toString();	
		final Tx originalMessage = TestUtils.makeMessageStanza(originalMessageId, "gm2552@cerner.com", 
				"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
		
		final String marshedTx = mapper.writeValueAsString(originalMessage);
		
		streamChannel.txInput().send(withPayload(marshedTx).build());

		verify(spyProducer, times(1)).send(any());
		
		final Message<?> msg = receive.receive(2000);

		@SuppressWarnings("unchecked")
		final Collection<String> errors = (Collection<String>)msg.getPayload();
		
		assertEquals(1, errors.size());
	}
}
