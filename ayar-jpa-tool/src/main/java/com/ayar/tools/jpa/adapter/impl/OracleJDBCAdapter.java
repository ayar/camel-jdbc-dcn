package com.ayar.tools.jpa.adapter.impl;

import java.sql.SQLException;
import java.util.Map;
import java.util.Observable;

import javax.sql.DataSource;

import org.apache.commons.lang3.Validate;

import com.ayar.tools.jpa.adapter.JDBCAdapter;
import com.ayar.tools.jpa.dcn.NotificationRegistration;

import oracle.jdbc.datasource.OracleDataSource;


public class OracleJDBCAdapter   implements JDBCAdapter{

	@Override
	public String getNextValQuery(String sequencename) {
		Validate.isTrue(sequencename!=null,"sequence should not be null!!! ");
		return " SELECT "+sequencename.trim()+".nextval from dual ";
	}

	@Override
	public NotificationRegistration createDatabaseChangeRegistration(DataSource ds, Map<String, Object> parameters) {
		return new OracleNotificationRegistration((OracleDataSource) ds, parameters);
	}

	@Override
	public String getNextVal(String sequencename) {
		// TODO Auto-generated method stub
		return sequencename.trim()+".nextval";
	}

	
	

}
