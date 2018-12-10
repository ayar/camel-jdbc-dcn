package com.ayar.jpatool.sample.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Conditional;

@ConditionalOnClass(name="com.impossibl.postgres.jdbc.PGDataSource")
public class PGDcnNotificationRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("dcn:mypostgres?query=USER_NOTIF").to("mock:result");

    }
}
