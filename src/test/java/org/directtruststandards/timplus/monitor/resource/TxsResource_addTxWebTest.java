package org.directtruststandards.timplus.monitor.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.directtruststandards.timplus.monitor.test.BaseTestPlan;
import org.directtruststandards.timplus.monitor.test.TestUtils;
import org.directtruststandards.timplus.monitor.test.TestWebApplication;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestWebApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@TestPropertySource(locations = "classpath:properties/timeoutChatMonitor.properties")
public class TxsResource_addTxWebTest
{
	@Autowired
	protected QueueChannel receive;
	
	@Autowired
	protected TestRestTemplate testRestTemplate;
	
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected Collection<Tx> getTxsToSubmit()
		{
			return Collections.emptyList();
		}
									
		@Override
		protected void performInner() throws Exception
		{
			
			final Collection<Tx> txs = getTxsToSubmit();
			if (txs != null)
				txs.forEach(tx -> 
			    {
			    	HttpEntity<Tx> requestEntity = new HttpEntity<>(tx);
			    	final ResponseEntity<Void> resp = testRestTemplate.exchange("/txs", HttpMethod.POST, requestEntity, Void.class);
			    	
					if (resp.getStatusCodeValue() != 201)
						throw new HttpClientErrorException(resp.getStatusCode());	
			    });

			doAssertions();
		}
		
		
		protected void doAssertions() throws Exception
		{
			
		}
	}	
	
	@Test
	public void testSingleRecipAMPReceived_assertConditionComplete() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected Collection<Tx> getTxsToSubmit()
			{
				final Collection<Tx> txs = new ArrayList<Tx>();
				
				// send original message
				final String originalMessageId = UUID.randomUUID().toString();	
				
				Tx originalMessage = TestUtils.makeMessageStanza(originalMessageId, "gm2552@cerner.com", 
						"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
				txs.add(originalMessage);

				// send AMP to original message
				Tx mdnMessage = TestUtils.makeAMPStanza(originalMessageId, "gm2552@direct.securehealthemail.com", 
						"gm2552@cerner.com", "", "gm2552@direct.securehealthemail.com");
				txs.add(mdnMessage);
				
				return txs;

			}
			
			
			protected void doAssertions() throws Exception
			{
				final Message<?> msg = receive.receive(2000);

				assertNull(msg);
			}
		}.perform();		
	}
	
	@Test
	public void testNoAMPReceived_assertError() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected Collection<Tx> getTxsToSubmit()
			{
				final Collection<Tx> txs = new ArrayList<Tx>();
				
				// send original message
				final String originalMessageId = UUID.randomUUID().toString();	
				
				Tx originalMessage = TestUtils.makeMessageStanza(originalMessageId, "gm2552@cerner.com", 
						"gm2552@direct.securehealthemail.com", org.jivesoftware.smack.packet.Message.Type.chat.name());
				txs.add(originalMessage);
				
				return txs;

			}
			
			
			protected void doAssertions() throws Exception
			{
				final Message<?> msg = receive.receive(2000);

				@SuppressWarnings("unchecked")
				final Collection<String> errors = (Collection<String>)msg.getPayload();
				
				assertEquals(1, errors.size());
			}
		}.perform();		
	}
}
