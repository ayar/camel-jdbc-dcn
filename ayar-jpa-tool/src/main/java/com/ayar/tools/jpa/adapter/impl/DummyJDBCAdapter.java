package com.ayar.tools.jpa.adapter.impl;

import java.util.Map;

import javax.sql.DataSource;

import com.ayar.tools.jpa.adapter.JDBCAdapter;
import com.ayar.tools.jpa.dcn.NotificationRegistration;

public class DummyJDBCAdapter implements JDBCAdapter {

	@Override
	public String getNextValQuery(String sequencename) {
		throw new RuntimeException("Sequence  is not supported by DummyAdapter");
	}


	@Override
	public NotificationRegistration createDatabaseChangeRegistration(DataSource ds, Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		throw new RuntimeException("DatabaseChangeNotification  is not supported by DummyAdapter");

	}


	@Override
	public String getNextVal(String sequencename) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Sequence  is not supported by DummyAdapter");
	}

}
