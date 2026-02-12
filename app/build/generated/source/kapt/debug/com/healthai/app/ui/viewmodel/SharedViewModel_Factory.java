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
public final class SharedViewModel_Factory implements Factory<SharedViewModel> {
  @Override
  public SharedViewModel get() {
    return newInstance();
  }

  public static SharedViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SharedViewModel newInstance() {
    return new SharedViewModel();
  }

  private static final class InstanceHolder {
    private static final SharedViewModel_Factory INSTANCE = new SharedViewModel_Factory();
  }
}
