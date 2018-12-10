package com.ayar.tools.jpa.identifier;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class CommonConfiguration {

	@Bean
	public SequenceIDGeneratorProcessor getIdGenerator() {
		return new SequenceIDGeneratorProcessor();
	}

}