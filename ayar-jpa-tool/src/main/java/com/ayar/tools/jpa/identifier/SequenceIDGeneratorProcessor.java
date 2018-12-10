package com.ayar.tools.jpa.identifier;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.ayar.tools.jpa.adapter.JDBCAdapter;
import com.ayar.tools.jpa.adapter.JDBCAdapterFactory;

import oracle.jdbc.pool.OracleDataSource;

@Component
@Aspect
public class SequenceIDGeneratorProcessor implements ApplicationContextAware{

	@Autowired
	public void setDataSource(DataSource dataSource) throws SQLException {
		this.defaultDataSource = dataSource;
	}

private Map<String, DataSource> datasources  = new ConcurrentHashMap<String, DataSource>() ;
private Map<String, JDBCAdapter> adapters = new ConcurrentHashMap<String, JDBCAdapter>() ;
	private DataSource defaultDataSource;
	private ApplicationContext applicationContext;
	
	DataSource getDataSource(String datasource) {
		DataSource  ds= datasources.get(datasource);
		 if (ds==null){
			 if ("default".equals(datasource)) {
				 ds = this.defaultDataSource;
			 }else{
			 ds =this.applicationContext.getBean(datasource, DataSource.class);
			 }
			 if (ds!=null){
				 datasources.put(datasource, ds);
			 }
		 }
		 //TODO :IF data source is null switching to  a default  datasource instead of exception
		 if (ds==null) throw new RuntimeException("unable to find a data source bean with name :" +datasource) ;
		 return ds;
	}
	JDBCAdapter getAdapter(String datasourcename) throws SQLException{
		JDBCAdapter adapter = adapters.get(datasourcename);
		if (adapter !=null) return adapter;
		adapter = JDBCAdapterFactory.getAdapter(getDataSource(datasourcename).getConnection());
		adapters.put(datasourcename, adapter);
		return adapter;
	}

	@Pointcut("execution(* *(..)) ")
	public void idPointcut() {
	}

	@Around("idPointcut() && @annotation(myAnnotation)")
	public Object getNextVal(ProceedingJoinPoint call,
			final SequenceIDGeneratorAnnotation myAnnotation) throws Throwable {
		MethodSignature signature = (MethodSignature) call.getSignature();
		Method method = signature.getMethod();
		SequenceIDGeneratorAnnotation annotation = method
				.getAnnotation(SequenceIDGeneratorAnnotation.class);
		Object val = call.proceed();
		Connection cnx = getDataSource(annotation.datasource()).getConnection();
		PreparedStatement pst = cnx.prepareStatement(getAdapter(annotation.datasource()).getNextValQuery(annotation.value()));

			ResultSet rs = pst.executeQuery();
			if (rs.next())
				return rs.getObject(1);
				//return rs.getLong(1);

		throw new RuntimeException("unsupported identifier");
	}

	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}