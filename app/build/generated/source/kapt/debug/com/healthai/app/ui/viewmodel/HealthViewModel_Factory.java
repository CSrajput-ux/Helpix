package com.healthai.app.ui.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
  @Override
  public HealthViewModel get() {
    return newInstance();
  }

  public static HealthViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HealthViewModel newInstance() {
    return new HealthViewModel();
  }

  private static final class InstanceHolder {
    private static final HealthViewModel_Factory INSTANCE = new HealthViewModel_Factory();
  }
}
