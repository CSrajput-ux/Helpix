package com.healthai.app.domain.usecase;

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
public final class AnalyzeCoughUseCase_Factory implements Factory<AnalyzeCoughUseCase> {
  @Override
  public AnalyzeCoughUseCase get() {
    return newInstance();
  }

  public static AnalyzeCoughUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AnalyzeCoughUseCase newInstance() {
    return new AnalyzeCoughUseCase();
  }

  private static final class InstanceHolder {
    private static final AnalyzeCoughUseCase_Factory INSTANCE = new AnalyzeCoughUseCase_Factory();
  }
}
