package com.healthai.app.di;

import com.healthai.app.data.local.dao.UserDao;
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
public final class AppModule_ProvideUserDaoFactory implements Factory<UserDao> {
  private final Provider<HealthDatabase> dbProvider;

  public AppModule_ProvideUserDaoFactory(Provider<HealthDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public UserDao get() {
    return provideUserDao(dbProvider.get());
  }

  public static AppModule_ProvideUserDaoFactory create(Provider<HealthDatabase> dbProvider) {
    return new AppModule_ProvideUserDaoFactory(dbProvider);
  }

  public static UserDao provideUserDao(HealthDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUserDao(db));
  }
}
