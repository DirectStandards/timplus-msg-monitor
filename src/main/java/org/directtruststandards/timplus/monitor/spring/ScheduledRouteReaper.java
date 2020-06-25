package org.directtruststandards.timplus.monitor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.store.MessageGroupStoreReaper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledRouteReaper
{
	@Autowired
	protected MessageGroupStoreReaper reaper;
	
	@Scheduled(fixedRateString = "${timplus.monitor.condition.reaper.period:3600000}")
	public void reapMessageStore()
	{
		reaper.run();
	}
}
