package com.ayar.tools.jpa.dcn;



import javax.sql.DataSource;

import org.apache.camel.LoggingLevel;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.ErrorHandler;
@UriParams
public class NotificationConfiguration implements Cloneable {
	@UriParam(description = "The data source to be use. A data source must be configured either on the component or endpoint.")
	private DataSource dataSource;
	@UriParam(label = "advanced", description = "Specifies a org.springframework.util.ErrorHandler to be invoked in case of any uncaught exceptions thrown while processing a Message."
			+ " By default these exceptions will be logged at the WARN level, if no errorHandler has been configured."
			+ " You can configure logging level and whether stack traces should be logged using errorHandlerLoggingLevel and errorHandlerLogStackTrace options."
			+ " This makes it much easier to configure, than having to code a custom errorHandler.")
	private ErrorHandler errorHandler;
	@UriParam(defaultValue = "WARN", label = "consumer,logging", description = "Allows to configure the default errorHandler logging level for logging uncaught exceptions.")
	private final LoggingLevel errorHandlerLoggingLevel = LoggingLevel.WARN;
	@UriParam(label = "consumer", defaultValue = "true", description = "Specifies whether the consumer container should auto-startup.")
	private final boolean autoStartup = true;
	@UriParam(label = "consumer,advanced", description = "Allows you to specify a custom task executor for consuming messages.")
	private TaskExecutor taskExecutor;
	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public NotificationConfiguration copy() {
		try {
			return (NotificationConfiguration) clone();
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeCamelException(e);
		}
	}
}