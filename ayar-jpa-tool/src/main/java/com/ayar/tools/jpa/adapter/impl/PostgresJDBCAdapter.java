package com.ayar.tools.jpa.adapter.impl;

import com.ayar.tools.jpa.adapter.JDBCAdapter;
import com.ayar.tools.jpa.dcn.NotificationRegistration;
import com.impossibl.postgres.jdbc.PGDataSource;
import org.apache.commons.lang3.Validate;

import javax.sql.DataSource;
import java.util.Map;

public class PostgresJDBCAdapter implements JDBCAdapter {

    @Override
    public String getNextValQuery(String sequencename) {
        Validate.isTrue(sequencename!=null,"sequence should not be null!!! ");
        return " SELECT nextval('"+sequencename.trim()+"') ";
    }

    @Override
    public NotificationRegistration createDatabaseChangeRegistration(DataSource ds, Map<String, Object> parameters) {
        return new PostgresNotificationRegistration((PGDataSource) ds, parameters);
    }

    @Override
    public String getNextVal(String sequencename) {
        // TODO Auto-generated method stub
        return "nextval('"+sequencename.trim()+"') ";
    }




}
