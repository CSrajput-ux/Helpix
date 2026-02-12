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
public final class LoginUserUseCase_Factory implements Factory<LoginUserUseCase> {
  @Override
  public LoginUserUseCase get() {
    return newInstance();
  }

  public static LoginUserUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LoginUserUseCase newInstance() {
    return new LoginUserUseCase();
  }

  private static final class InstanceHolder {
    private static final LoginUserUseCase_Factory INSTANCE = new LoginUserUseCase_Factory();
  }
}
