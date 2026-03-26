package com.healthai.app.data.repository;

import com.healthai.app.data.local.dao.HealthMetricDao;
import com.healthai.app.data.remote.api.HealthApiService;
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

  private final Provider<HealthApiService> apiProvider;

  public HealthRepositoryImpl_Factory(Provider<HealthMetricDao> daoProvider,
      Provider<HealthApiService> apiProvider) {
    this.daoProvider = daoProvider;
    this.apiProvider = apiProvider;
  }

  @Override
  public HealthRepositoryImpl get() {
    return newInstance(daoProvider.get(), apiProvider.get());
  }

  public static HealthRepositoryImpl_Factory create(Provider<HealthMetricDao> daoProvider,
      Provider<HealthApiService> apiProvider) {
    return new HealthRepositoryImpl_Factory(daoProvider, apiProvider);
  }

  public static HealthRepositoryImpl newInstance(HealthMetricDao dao, HealthApiService api) {
    return new HealthRepositoryImpl(dao, api);
  }
}
