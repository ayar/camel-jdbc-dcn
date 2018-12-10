package com.ayar.tools.jpa.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BatchRepository<T, ID extends Serializable> extends
		JpaRepository<T, ID> {

	int batchSave(List<T> records);

	int batchSave(String sqlQuery, List<T> records);

}