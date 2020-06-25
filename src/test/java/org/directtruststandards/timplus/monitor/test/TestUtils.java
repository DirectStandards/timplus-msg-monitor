package org.directtruststandards.timplus.monitor.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.directtruststandards.timplus.monitor.tx.model.TxDetail;
import org.directtruststandards.timplus.monitor.tx.model.TxDetailType;
import org.directtruststandards.timplus.monitor.tx.model.TxStanzaType;

public class TestUtils
{	
	protected static Map<TxDetailType, TxDetail> makeGeneralDetails(String msgId, String from, String recips, String type)
	{
		final Map<TxDetailType, TxDetail> details = new HashMap<>();
		
		if(!StringUtils.isEmpty(msgId))
			details.put(TxDetailType.MSG_ID, new TxDetail(TxDetailType.MSG_ID, msgId));
	
		if(!StringUtils.isEmpty(from))
			details.put(TxDetailType.FROM, new TxDetail(TxDetailType.FROM, from));
		
		if(!StringUtils.isEmpty(recips))
			details.put(TxDetailType.RECIPIENTS, new TxDetail(TxDetailType.RECIPIENTS, recips));
		
		if(!StringUtils.isEmpty(type))
			details.put(TxDetailType.TYPE, new TxDetail(TxDetailType.TYPE, type));	
	
		return details;
	}
	
	public static Tx makeMessageStanza(String msgId, String from, String recips, String type)
	{
		final Map<TxDetailType, TxDetail> details = makeGeneralDetails(msgId, from, recips, type);
		
		return new Tx(TxStanzaType.MESSAGE, details);
	}
	
	public static Tx makeErrorMessageStanza(String msgId, String from, String recips, String type, String originalRecip)
	{
		final Map<TxDetailType, TxDetail> details = makeGeneralDetails(msgId, from, recips, type);	
		details.put(TxDetailType.ORIGINAL_RECIPIENT, new TxDetail(TxDetailType.ORIGINAL_RECIPIENT, originalRecip));
		
		return new Tx(TxStanzaType.MESSAGE_ERROR, details);
	}
	
	public static Tx makeAMPStanza(String msgId, String from, String recips, String type, String originalRecip)
	{
		final Map<TxDetailType, TxDetail> details = makeGeneralDetails(msgId, from, recips, type);	
		details.put(TxDetailType.ORIGINAL_RECIPIENT, new TxDetail(TxDetailType.ORIGINAL_RECIPIENT, originalRecip));
		
		return new Tx(TxStanzaType.AMP, details);
	}
}
