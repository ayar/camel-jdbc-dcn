package com.ayar.tools.jpa.dcn;



import java.util.Observable;
import java.util.Observer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.impl.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@ManagedResource(description = "Managed DataBase Notification Consumer")
public class NotificationConsumer extends DefaultConsumer implements Observer {
	private static Logger LOG = LoggerFactory
			.getLogger(NotificationConsumer.class);
	public NotificationConsumer(final NotificationEndpoint endpoint,
			final Processor processor) {
		super(endpoint, processor);
		// TODO Auto-generated constructor stub
	}
	@Override
	public NotificationEndpoint getEndpoint() {
		return (NotificationEndpoint) super.getEndpoint();
	}
	@Override
	public void start() throws Exception {
		getEndpoint().connect(this);
		super.start();
	}
	@Override
	public void stop() throws Exception {
		getEndpoint().disconnect(this);
		super.stop();
	}
	
	
	@Override
	public void update(Observable o, Object event) {
		final Exchange exchange = getEndpoint().createExchange();
		exchange.getIn().setBody(event, event.getClass());
		try {
			getProcessor().process(exchange);
		} catch (final Exception e) {
			LOG.warn("Cannot process exchange", e);
		}
	}
}