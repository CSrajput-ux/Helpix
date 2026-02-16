package com.healthai.app.di;

import com.healthai.app.data.local.dao.HealthMetricDao;
import com.healthai.app.data.local.database.HealthDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideHealthMetricDaoFactory implements Factory<HealthMetricDao> {
  private final Provider<HealthDatabase> dbProvider;

  public AppModule_ProvideHealthMetricDaoFactory(Provider<HealthDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public HealthMetricDao get() {
    return provideHealthMetricDao(dbProvider.get());
  }

  public static AppModule_ProvideHealthMetricDaoFactory create(
      Provider<HealthDatabase> dbProvider) {
    return new AppModule_ProvideHealthMetricDaoFactory(dbProvider);
  }

  public static HealthMetricDao provideHealthMetricDao(HealthDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHealthMetricDao(db));
  }
}
