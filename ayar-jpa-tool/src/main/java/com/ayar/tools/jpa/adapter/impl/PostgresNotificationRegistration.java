package com.ayar.tools.jpa.adapter.impl;

import com.ayar.tools.jpa.dcn.NotificationRegistration;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;



import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

public class PostgresNotificationRegistration extends NotificationRegistration implements PGNotificationListener {

	PGConnection connection;


	public PostgresNotificationRegistration(PGDataSource ds, Map<String, Object> parameters)
	{
		final Properties props = new Properties();
		props.putAll(parameters);
		try {
			this.connection =  (PGConnection) ds.getConnection();
			connection.addNotificationListener(this);
		} catch (SQLException e) {
			throw new RuntimeException("unable to create database change registration", e);
		}

	}

	@Override
	public void register(String name, boolean discardOld) {
		this.name = name;
		this.discardOld = discardOld;
		Statement statement;
		try {
			statement = this.connection.createStatement();
			statement.execute("LISTEN " + this.name);
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException("unable to register "  + name ,e );
		}
		
		
	}

	String name;
	boolean discardOld;

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void unregister() {
		if (this.connection !=null ) {
			try {
				Statement statement = this.connection.createStatement();
				statement.execute("UNLISTEN  " + this.name);
				statement.close();
			} catch (final Exception ex) {
				// ignore
			}
		}
	}


	


	@Override
	public void notification(int processId, String channelName, String payload) {
		setChanged();
		notifyObservers(new PGNotifcation(processId,channelName,payload));
	}
}
