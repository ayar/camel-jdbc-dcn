package com.ayar.tools.jpa.dcn;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.HeaderFilterStrategyComponent;
import org.apache.camel.util.IntrospectionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
//https://docs.oracle.com/cd/B28359_01/appdev.111/b28424/adfns_cqn.htm#BGBIAEBB
public class NotificationComponent extends HeaderFilterStrategyComponent
		implements ApplicationContextAware {
	private static final Logger LOG = LoggerFactory
			.getLogger(NotificationComponent.class);
	private ApplicationContext applicationContext;
	private NotificationConfiguration configuration;
	public NotificationComponent() {
		super(NotificationEndpoint.class);
		// TODO Auto-generated constructor stub
	}
	public NotificationComponent(final Class<? extends Endpoint> endpointClass) {
		super(endpointClass);
	}
	public NotificationComponent(final CamelContext context) {
		super(context, NotificationEndpoint.class);
	}
	public NotificationComponent(final NotificationConfiguration configuration) {
		this();
		this.configuration = configuration;
	}
	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Override
	protected Endpoint createEndpoint(final String uri, final String remaining,
			final Map<String, Object> parameters) throws Exception {
		// copy the configuration as each endpoint can customize its own version
		final NotificationConfiguration endpointConfiguration = getConfiguration()
				.copy();
		if (remaining != null && !"".equals(remaining)) {
			final DataSource ds = applicationContext.getBean(remaining,
					DataSource.class);
			if (ds != null) {
				endpointConfiguration.setDataSource(ds);
			}
		}
		final Map<String, Object> params = IntrospectionSupport
				.extractProperties(parameters, "OracleConnection.");
		if (params != null && !params.isEmpty()) {
			setProperties(endpointConfiguration, parameters);
		}
		final NotificationEndpoint endpoint = new NotificationEndpoint(uri,
				remaining, this, endpointConfiguration);
		endpoint.setParameters(params);
		return endpoint;
	}
	public NotificationConfiguration getConfiguration() {
		if (configuration == null) {
			// default configuration
			configuration = new NotificationConfiguration();
			final Map<String, DataSource> beansOfTypeConnectionFactory = applicationContext
					.getBeansOfType(DataSource.class);
			if (!beansOfTypeConnectionFactory.isEmpty()) {
				final DataSource cf = beansOfTypeConnectionFactory
						.values().iterator().next();
				configuration.setDataSource(cf);
			}
		}
		return configuration;
	}
	/*
	 * Use a shared configuration
	 */
	public void setConfiguration(final NotificationConfiguration configuration) {
		this.configuration = configuration;
	}
}