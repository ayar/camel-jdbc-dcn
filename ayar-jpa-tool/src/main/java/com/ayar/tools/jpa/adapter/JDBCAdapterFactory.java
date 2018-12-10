package com.ayar.tools.jpa.adapter;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ayar.tools.jpa.adapter.impl.DummyJDBCAdapter;


public final class JDBCAdapterFactory {

    private static final Logger LOG = LoggerFactory.getLogger(JDBCAdapterFactory.class);
    private static FactoryFinder factoryFinder = new FactoryFinder("META-INF/services/com/ayar/tools/jpa/");
    private static Map<String,JDBCAdapter> adapters = new ConcurrentHashMap<String, JDBCAdapter>() ; 
    private JDBCAdapterFactory() {
    }
    
    public static JDBCAdapter getAdapter(Connection connection) {
        JDBCAdapter adapter = null;
        try {

            // Make the filename file system safe.
            String driverName = connection.getMetaData().getDriverName();
            driverName = driverName.replaceAll("[^a-zA-Z0-9\\-]", "_").toLowerCase();
             adapter = adapters.get(driverName);
           if ( adapter==null)  { 
            try {
                adapter = (JDBCAdapter) factoryFinder.newInstance(driverName);
                adapters.put(driverName, adapter);
                LOG.info("Database driver recognized: [" + driverName + "]");
            } catch (Throwable e) {
                LOG.warn("Database driver NOT recognized: [" + driverName
                        + "].");
                adapter = new DummyJDBCAdapter();
            }
            adapters.put(driverName, adapter);
           }
          
           
        } catch (SQLException e) {
            LOG.warn("JDBC error occurred while trying to detect database type: "
                            + e.getMessage());
            log("Failure details: ", e);
            //ADAPTER WILL BE NULL
        }

     if (adapter ==null){
    	 LOG.warn("Factory is returning the DummyJDBCAdapter");
    	 adapter = new DummyJDBCAdapter();
     }
        
        return adapter;
    }
    
    public static void log(String msg, SQLException e) {
        if (LOG.isDebugEnabled()) {
            String s = msg + e.getMessage();
            while (e.getNextException() != null) {
                e = e.getNextException();
                s += ", due to: " + e.getMessage();
            }
            LOG.debug(s, e);
        }
    }
}