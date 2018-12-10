package com.ayar.tools.jpa.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.ayar.tools.jpa.adapter.JDBCAdapter;
import com.ayar.tools.jpa.adapter.JDBCAdapterFactory;

public class BatchRepositoryImpl<T, ID extends Serializable> extends
		SimpleJpaRepository<T, ID> implements BatchRepository<T, ID> {

	private NamedParameterJdbcTemplate jdbcTemplate;
	private JDBCAdapter adapter;
	
	@Autowired
	public void setDataSource(DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.adapter=JDBCAdapterFactory.getAdapter(dataSource.getConnection());
	}

	private DataSource dataSource;

	private final String insertsqlBatch;

	public BatchRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.insertsqlBatch = buildInsertSQl(domainClass);
	}

	private String buildInsertSQl(Class outputClass) {
		Validate.isTrue(outputClass.isAnnotationPresent(Entity.class),
				"Only annotated classes are supported");
		final Table table = (Table) outputClass.getAnnotation(Table.class);
		final String tablename = table == null || table.name() == null ? outputClass
				.getName().toUpperCase() : table.name();
		final List<String> columns = new ArrayList<String>();
		final List<String> values = new ArrayList<String>();
		final Field[] fields = outputClass.getDeclaredFields();
		for (final Field field : fields) {

			if (!field.isAnnotationPresent(Transient.class)
					&& !Modifier.isStatic(field.getModifiers())) {
				if (field.isAnnotationPresent(Column.class)) {
					if (field.getAnnotation(Column.class).insertable()) {
						columns.add(field.getAnnotation(Column.class).name());
						if (field.isAnnotationPresent(GeneratedValue.class)) {
							if (field
									.isAnnotationPresent(SequenceGenerator.class)) {
								values.add(adapter.getNextVal(field.getAnnotation(
										SequenceGenerator.class).sequenceName()
										));
							} else if (field
									.isAnnotationPresent(GenericGenerator.class)) {
								values.add(adapter.getNextVal(Arrays
										.stream(field.getAnnotation(
												GenericGenerator.class)
												.parameters())
										.filter(e -> "sequence_name"
												.equalsIgnoreCase(e.name()))
										.findFirst().get().value()));
							} else {
								throw new RuntimeException(
										"Unsupproted Generator Annotation");
							}
						} else {
							values.add(":"
									+ field.getAnnotation(Column.class).name());
						}

					}
				} else {
					columns.add(field.getName());
					values.add(":" + field.getName());
				}
			}
		}
		return new StringBuilder("INSERT INTO ")
				.append(tablename)
				.append(" (")
				.append(String.join(", ", columns).replaceAll(
						"([a-z])([A-Z]+)", "$1_$2")).append(") values (")
				.append(String.join(" ,", values)).append(")").toString();

	}

	@Override
	@Transactional
	public int batchSave(List<T> records) {
		return batchSave(this.insertsqlBatch, records);

	}

	@Override
	public int batchSave(String sql, List<T> records) {
		Validate.notNull(dataSource, "dataSource  must not be null");
		final SqlParameterSource[] batch = SqlParameterSourceUtils
				.createBatch(records.toArray());
		try {
			final int[] rows = this.jdbcTemplate.batchUpdate(sql, batch);
			return sumRows(rows);
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private int sumRows(final int[] rows) {
		int rowCount = 0;
		for (final int row : rows) {
			rowCount += row;
		}
		return rowCount;
	}

}
