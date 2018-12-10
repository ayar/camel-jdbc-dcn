package com.ayar.tools.jpa.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class BaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable>
		extends JpaRepositoryFactoryBean<R, T, I> implements
		ApplicationContextAware {

	public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(
			final EntityManager entityManager) {
		return new BaseRepositoryFactory(entityManager);
	}

	private static class BaseRepositoryFactory<T, I extends Serializable>
			extends JpaRepositoryFactory {

		private final EntityManager em;

		public BaseRepositoryFactory(final EntityManager em) {
			super(em);
			this.em = em;
		}

		protected Object getTargetRepository(final RepositoryMetadata metadata) {

			final Object repository = new BatchRepositoryImpl<T, I>(
					(Class<T>) metadata.getDomainType(), em);
			getContext().getAutowireCapableBeanFactory().autowireBean(
					repository);
			return repository;
		}

		@Override
		protected Class<?> getRepositoryBaseClass(
				final RepositoryMetadata metadata) {
			return BatchRepositoryImpl.class;
		}
	}

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(final ApplicationContext context)
			throws BeansException {
		this.context = context;
	}

	public static ApplicationContext getContext() {
		return context;
	}

}