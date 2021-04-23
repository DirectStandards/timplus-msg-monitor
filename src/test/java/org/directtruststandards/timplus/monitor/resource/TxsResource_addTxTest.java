package org.directtruststandards.timplus.monitor.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;

import reactor.core.publisher.Mono;

public class TxsResource_addTxTest
{
	@Test
	public void testAddTx_nullMessageChannel_assertExcecption()
	{
		Assertions.assertThrows(IllegalStateException.class, () ->
		{
			final Tx tx = mock(Tx.class);
			
			new TxsResource(null).addTx(tx);
		});
	}
	
	@Test
	public void testAddTx_exceptionInSubmission_assertErrorCode()
	{
		final MessageChannel channel = mock(MessageChannel.class);
		doThrow(new RuntimeException("")).when(channel).send(any());
		
		final Tx tx = mock(Tx.class);
		
		final TxsResource resource = new TxsResource(channel);

		final ResponseEntity<Mono<Void>> res = resource.addTx(tx);
		
		assertEquals(500, res.getStatusCodeValue());
	}
	
	@Test
	public void testAddTx_assertCreatedCode()
	{
		final MessageChannel channel = mock(MessageChannel.class);
		
		final Tx tx = mock(Tx.class);
		
		final TxsResource resource = new TxsResource(channel);
		
		final ResponseEntity<Mono<Void>> res = resource.addTx(tx);
		
		assertEquals(201, res.getStatusCodeValue());
	}
}
