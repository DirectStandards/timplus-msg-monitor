package org.directtruststandards.timplus.monitor.resource;

import org.directtruststandards.timplus.monitor.tx.model.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("txs")
public class TxsResource
{
	/**
	 * Cache definition for no caching of responses.
	 */
	protected static final CacheControl noCache;
	
	@Qualifier("monitorStart")
    @Autowired
	protected MessageChannel msgChannel;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TxsResource.class);
	
	static
	{
		noCache = CacheControl.noCache();
	}
	
    /**
     * Constructor
     */
	public TxsResource()
	{
		
	}
	
	/**
	 * Constructor 
	 * @param msgChannel Message channel used for placing message into the integration route.
	 */
	public TxsResource(MessageChannel msgChannel)
	{
		this.msgChannel = msgChannel;
	}
	
	/**
	 * Sets the message channel.
	 * @param msgChannel The message channel.
	 */
	public void setMessageChannel(MessageChannel msgChannel)
	{
		this.msgChannel = msgChannel;
	}
	
	/**
	 * Adds a Tx message into the system
	 * @param tx The message to add.
	 * @return Spring MVC response object containing the http status code.  If the message is successfully added to the integration route, then status 201 (created)
	 * is returned.  500 is returned if an error occurs.
	 */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)         
    public ResponseEntity<Mono<Void>> addTx(@RequestBody Tx tx)
    {
    	///CLOVER:OFF
    	if (LOGGER.isTraceEnabled())
    		LOGGER.trace("Attempting to add Tx");
    	///CLOVER:ON
    	
    	if (msgChannel == null)
    		throw new IllegalStateException("Message channel cannot be null.  Please examine the txs resource configuration");
    	
    	try
    	{
    		msgChannel.send(MessageBuilder.withPayload(tx).build());
    	}
    	catch (Throwable t)
    	{
    		LOGGER.error("Failed to add Tx message", t);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).cacheControl(noCache).build();
    	}
    	
    	///CLOVER:OFF
    	if (LOGGER.isTraceEnabled())
    		LOGGER.trace("Tx added");
    	///CLOVER:ON
    	
		return ResponseEntity.status(HttpStatus.CREATED).cacheControl(noCache).build();
    }
    
}
