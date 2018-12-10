package com.ayar.tools.jpa.adapter;

import java.util.Map;

import javax.sql.DataSource;

import com.ayar.tools.jpa.dcn.NotificationRegistration;;

public interface JDBCAdapter{
	String getNextValQuery(String sequencename);
	String getNextVal(String sequencename);

	NotificationRegistration createDatabaseChangeRegistration(DataSource ds,Map<String, Object> parameters);
}
