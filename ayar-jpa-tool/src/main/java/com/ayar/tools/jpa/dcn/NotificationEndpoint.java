package com.ayar.tools.jpa.dcn;


import java.util.Map;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

import com.ayar.tools.jpa.adapter.JDBCAdapter;
@UriEndpoint(firstVersion = "1.0.0", scheme = "dcn", title = "DCNNOTIFICATION", syntax = "dcn:dataSourceName", consumerOnly = true, label = "database,sql,oracle,notification")
public class NotificationEndpoint extends DefaultEndpoint {
	@UriParam
	private NotificationConfiguration configuration;
	@UriParam
	private String datasourceName;
	private NotificationRegistration registration;
	@UriParam(label = "consumer,advanced", defaultValue = "true", description = "If the option is true, Db Notification will remove old  registration, if it receive  notification with different regId")
	private boolean discardOld = true;
	@UriParam(label = "consumer", description = "sql query")
	@Metadata(required = "true")
	private String query;
	@UriParam(label = "consumer,advanced", prefix = "dcn.", multiValue = true, description = "for  the full description  please visit  datasourceprovider  website oracle : https://docs.oracle.com/cd/E11882_01/appdev.112/e13995/oracle/jdbc/OracleConnection.html ,postgres: https://www.postgresql.org/docs/9.0/static/sql-notify.html,sql server :  todo,...")
	private Map<String, Object> parameters;
	public NotificationConfiguration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(final NotificationConfiguration configuration) {
		this.configuration = configuration;
	}
	
	
	public String getDatasourceName() {
		return datasourceName;
	}
	public void setDatasourceName(final String datasourceName) {
		this.datasourceName = datasourceName;
	}
	public boolean isDiscardOld() {
		return discardOld;
	}
	public void setDiscardOld(final boolean discardOld) {
		this.discardOld = discardOld;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(final String query) {
		this.query = query;
	}

	JDBCAdapter adapter;
	public NotificationEndpoint(final String endpointUri,
			final String datasourceName, final NotificationComponent component,
			final NotificationConfiguration configuration) {
		super(endpointUri, component);
		this.configuration = configuration;
		this.datasourceName = datasourceName;
	}
	@Override
	public Consumer createConsumer(final Processor processor) throws Exception {
		final NotificationConsumer consumer = new NotificationConsumer(this,
				processor);
		configureConsumer(consumer);
		return consumer;
	}
	@Override
	public Producer createProducer() throws Exception {
		throw new UnsupportedOperationException(
				"Producer is not supported by  Notification component");
	}
	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	protected String createEndpointUri() {
		return datasourceName != null ? "dcn:" + datasourceName : "dcn";
	}
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(final Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	NotificationRegistration dcn;
	
	

	
	NotificationRegistration getRegistration() {
		if ( registration==null){
			registration =this.adapter.createDatabaseChangeRegistration(configuration.getDataSource(), parameters);
		}
		return registration;
	}

	
	@Override
	public Exchange createExchange() {
		Exchange exchange = super.createExchange();
		exchange.getIn().setHeaders(getParameters());
		exchange.getIn().setHeader("DCN_QUERY", getQuery());
		return exchange;
	}

	@Override
	protected void doStart() throws Exception {
        getRegistration().register(query,this.discardOld);		
		super.doStart();
	}
	@Override
	protected void doStop() throws Exception {
		if( this.registration !=null){
			this.registration.unregister();;
		}
		super.stop();
	}
	
	/************************** Notification Connections **************************/
	public void connect(final NotificationConsumer consumer) throws Exception {
		getRegistration().addObserver(consumer);
	}
	public void disconnect(final NotificationConsumer consumer)
			throws Exception {
		try {
			if (registration !=null){
				registration.deleteObserver(consumer);
			}
		} catch (final Exception ex) {
			// ignore
		}
	}
}