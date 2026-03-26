package com.healthai.app.ui.viewmodel;

import com.healthai.app.data.remote.HealthConnectManager;
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
public final class HealthViewModel_Factory implements Factory<HealthViewModel> {
  private final Provider<HealthConnectManager> healthConnectManagerProvider;

  public HealthViewModel_Factory(Provider<HealthConnectManager> healthConnectManagerProvider) {
    this.healthConnectManagerProvider = healthConnectManagerProvider;
  }

  @Override
  public HealthViewModel get() {
    return newInstance(healthConnectManagerProvider.get());
  }

  public static HealthViewModel_Factory create(
      Provider<HealthConnectManager> healthConnectManagerProvider) {
    return new HealthViewModel_Factory(healthConnectManagerProvider);
  }

  public static HealthViewModel newInstance(HealthConnectManager healthConnectManager) {
    return new HealthViewModel(healthConnectManager);
  }
}
