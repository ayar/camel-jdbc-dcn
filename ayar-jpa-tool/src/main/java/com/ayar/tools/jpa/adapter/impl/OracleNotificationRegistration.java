package com.ayar.tools.jpa.adapter.impl;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import com.ayar.tools.jpa.dcn.NotificationRegistration;


import oracle.jdbc.datasource.OracleDataSource;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.internal.OracleStatement;

public class OracleNotificationRegistration extends NotificationRegistration implements DatabaseChangeListener {

	OracleConnection connection;
	DatabaseChangeRegistration dcr;

	public OracleNotificationRegistration(OracleDataSource ds, Map<String, Object> parameters) 
	{
		final Properties props = new Properties();
		props.putAll(parameters);
		try {
			this.connection = (OracleConnection) ds.getConnection();
			dcr = this.connection.registerDatabaseChangeNotification(props);
		} catch (SQLException e) {
			throw new RuntimeException("unable to create database change registration", e);
		}

	}

	@Override
	public void register(String query, boolean discardOld) {
		this.query = query;
		this.discardOld = discardOld;
		OracleStatement statement;
		try {
			statement = (OracleStatement)this.connection.createStatement();
			statement.setDatabaseChangeRegistration(this.dcr);
			statement.execute(this.query);
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException("unable to register "  + query ,e );
		}
		
		
	}

	String query;
	boolean discardOld;

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void unregister() {
		if (this.connection !=null && dcr != null) {
			try {
				this.connection.unregisterDatabaseChangeNotification(this.dcr);
			} catch (final Exception ex) {
				// ignore
			}
		}
	}

	@Override
	public void onDatabaseChangeNotification(DatabaseChangeEvent event) {

			 			if (event != null && discardOld
			 					&& this.dcr.getRegId() != event.getRegId()) {
			 				discard(event.getRegistrationId());
			 				return;
			 			}
			 		
		setChanged();
		notifyObservers(event);
	}
	
	 void discard(final int registrationid) {
		 try {
			this.connection.unregisterDatabaseChangeNotification(registrationid);
		} catch (SQLException e) {
			// ignore
		}
	 }

}
