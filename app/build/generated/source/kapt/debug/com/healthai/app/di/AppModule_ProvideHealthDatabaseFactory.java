package com.healthai.app.di;

import android.app.Application;
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
public final class AppModule_ProvideHealthDatabaseFactory implements Factory<HealthDatabase> {
  private final Provider<Application> appProvider;

  public AppModule_ProvideHealthDatabaseFactory(Provider<Application> appProvider) {
    this.appProvider = appProvider;
  }

  @Override
  public HealthDatabase get() {
    return provideHealthDatabase(appProvider.get());
  }

  public static AppModule_ProvideHealthDatabaseFactory create(Provider<Application> appProvider) {
    return new AppModule_ProvideHealthDatabaseFactory(appProvider);
  }

  public static HealthDatabase provideHealthDatabase(Application app) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHealthDatabase(app));
  }
}
