package org.directtruststandards.timplus.monitor.condition;

import static org.junit.Assert.assertEquals;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.integration.store.MessageGroup;


public class DecayingTimeoutCondition_getTimeoutTest
{
	@Test
	public void testGetTimeout()
	{
		final long exchangeStartTime = System.currentTimeMillis();
		
		final DecayingTimeoutCondition cond = new DecayingTimeoutCondition(10000)
		{
			@Override
			protected long getCurrentTime()
			{
				return exchangeStartTime + 1000;
			}
		};
		
		final MessageGroup group = mock(MessageGroup.class);
		when(group.getTimestamp()).thenReturn(exchangeStartTime);
		
		assertEquals(9000, cond.getTimeout(group));
	}
	
	@Test
	public void testGetTimeout_zeroTimeRemaining_assert1ms()
	{
		final long exchangeStartTime = System.currentTimeMillis();
		
		final DecayingTimeoutCondition cond = new DecayingTimeoutCondition(10000)
		{
			@Override
			protected long getCurrentTime()
			{
				return exchangeStartTime + 10000;
			}
		};
		
		final MessageGroup group = mock(MessageGroup.class);
		when(group.getTimestamp()).thenReturn(exchangeStartTime);
		
		assertEquals(1, cond.getTimeout(group));
	}
	
	@Test
	public void testGetTimeout_negativeTimeRemaining_assert1ms()
	{
		final long exchangeStartTime = System.currentTimeMillis();
		
		final DecayingTimeoutCondition cond = new DecayingTimeoutCondition(10000)
		{
			@Override
			protected long getCurrentTime()
			{
				return exchangeStartTime + 10001;
			}
		};
		
		final MessageGroup group = mock(MessageGroup.class);
		when(group.getTimestamp()).thenReturn(exchangeStartTime);
		
		assertEquals(1, cond.getTimeout(group));
	}
}
