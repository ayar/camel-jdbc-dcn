package com.ayar.jpatool.sample.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(name="com.impossibl.postgres.jdbc.PGDataSource")
public class PGDatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(PGDatabaseConfiguration.class);



@Bean
@Qualifier("mypostgres")
DataSource getDataSource() {
   // TODO create  your data source
    return null;
}

}
