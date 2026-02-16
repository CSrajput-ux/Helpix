package com.healthai.app.data.repository;

import com.healthai.app.data.local.dao.HealthMetricDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class HealthRepositoryImpl_Factory implements Factory<HealthRepositoryImpl> {
  private final Provider<HealthMetricDao> daoProvider;

  public HealthRepositoryImpl_Factory(Provider<HealthMetricDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public HealthRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static HealthRepositoryImpl_Factory create(Provider<HealthMetricDao> daoProvider) {
    return new HealthRepositoryImpl_Factory(daoProvider);
  }

  public static HealthRepositoryImpl newInstance(HealthMetricDao dao) {
    return new HealthRepositoryImpl(dao);
  }
}
